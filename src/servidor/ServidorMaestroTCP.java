package servidor;

import modelo.*;
import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Sistema maestro: escucha conexiones TCP de los pisos (clientes) y administra
// los recursos globales del estacionamiento (casetas, cupo Ap, cabina del elevador).
// Cada piso mantiene una conexión persistente atendida por un ManejadorPiso.
public class ServidorMaestroTCP extends AgenteBase {
    private final int puerto;
    private final RecursosMaestro recursosM;
    private volatile boolean activo = true;
    private ServerSocket serverSocket;

    public ServidorMaestroTCP(String id, int puerto, RecursosMaestro recursosM) {
        super(id, "Servidor Maestro");
        this.puerto = puerto;
        this.recursosM = recursosM;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(puerto);
            estadoActual = EstadoBPMN.ESCUCHANDO_PISOS;
            while (activo && !Thread.currentThread().isInterrupted()) {
                try {
                    new Thread(new ManejadorPiso(serverSocket.accept())).start();
                } catch (SocketException se) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error en ServidorMaestroTCP: " + e.getMessage());
        } finally {
            estadoActual = EstadoBPMN.FINALIZADO;
        }
    }

    public void detener() {
        activo = false;
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
    }

    // Sesión TCP de un piso: registra, otorga y libera recursos del maestro.
    // Si la conexión cae, libera todo lo que el piso tenía tomado.
    private class ManejadorPiso implements Runnable {
        private final Socket socket;
        private final Set<String> sostenidos = ConcurrentHashMap.newKeySet();
        private int idPiso = -1;

        ManejadorPiso(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                String linea;
                while ((linea = in.readLine()) != null) {
                    out.println(procesar(linea));
                }
            } catch (IOException ignored) {
            } finally {
                if (idPiso > 0) {
                    recursosM.liberarTodo(idPiso, sostenidos);
                }
                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private String procesar(String linea) {
            String[] partes = linea.split(":");
            return switch (partes[0]) {
                case "REGISTRAR_PISO" -> {
                    int mCu = partes.length > 1 ? Integer.parseInt(partes[1]) : 4;
                    idPiso = recursosM.registrarPiso(mCu);
                    Thread.currentThread().setName("Sesion-Piso-" + idPiso);
                    yield "PISO:" + idPiso;
                }
                case "PEDIR" -> {
                    if (idPiso < 1 || partes.length < 2) yield "ERR:NO_REGISTRADO";
                    else if (recursosM.intentarEntrar(idPiso, partes[1])) {
                        sostenidos.add(partes[1]);
                        yield "OK";
                    } else yield "OCUPADO";
                }
                case "SOLTAR" -> {
                    if (partes.length > 1) {
                        recursosM.salir(idPiso, partes[1]);
                        sostenidos.remove(partes[1]);
                    }
                    yield "OK";
                }
                case "ESTADO" -> recursosM.resumenLinea();
                default -> "ERR:COMANDO_DESCONOCIDO";
            };
        }
    }
}
