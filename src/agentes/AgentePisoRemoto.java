package agentes;

import modelo.*;
import java.io.*;
import java.net.*;

public class AgentePisoRemoto extends AgenteBase {
    private final int puerto;
    private volatile boolean activo = true;
    private ServerSocket serverSocket;

    public AgentePisoRemoto(String id, int puerto, GestorRecursos recursos, ConfiguracionSimulador config) {
        super(id, "PisoRemoto(TCP)", recursos, config);
        this.puerto = puerto;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(puerto);
            estadoActual = EstadoBPMN.CIRCULANDO_CALLE; // Escuchando peticiones
            while (activo && !Thread.currentThread().isInterrupted()) {
                try (Socket cliente = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                     PrintWriter out = new PrintWriter(cliente.getOutputStream(), true)) {
                    String msg = in.readLine();
                    if (msg != null) {
                        out.println("ACK_PISO_1:" + msg);
                    }
                } catch (SocketException se) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error en AgentePisoRemoto: " + e.getMessage());
        } finally {
            estadoActual = EstadoBPMN.FINALIZADO;
        }
    }

    public void detener() {
        activo = false;
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
    }
}