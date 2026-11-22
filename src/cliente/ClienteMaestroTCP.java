package cliente;

import java.io.*;
import java.net.Socket;

// Cliente del piso: mantiene una conexión persistente con el sistema maestro
// para pedir y soltar sus recursos globales (casetas, cupo Ap, cabina del elevador).
// Si el maestro no responde o la conexión cae, reintenta hasta lograrlo.
public class ClienteMaestroTCP {
    private final String host;
    private final int puerto;
    private int idPiso = -1;
    private int mCubiculosAuto;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClienteMaestroTCP(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    // Reintenta hasta que el maestro registre este piso.
    public synchronized void conectar(int mCubiculosAuto) throws InterruptedException {
        this.mCubiculosAuto = mCubiculosAuto;
        while (idPiso <= 0) {
            try {
                socket = new Socket(host, puerto);
                socket.setSoTimeout(3000);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String respuesta = enviarLinea("REGISTRAR_PISO:" + mCubiculosAuto);
                if (respuesta != null && respuesta.startsWith("PISO:")) {
                    idPiso = Integer.parseInt(respuesta.substring(5));
                    return;
                }
            } catch (IOException e) {
                cerrar();
            }
            cerrar();
            Thread.sleep(1000);
        }
    }

    // PEDIR con reintento (polling): regresa cuando el maestro otorga el recurso.
    // El sleep queda fuera del bloqueo del socket para no congelar los SOLTAR.
    public void pedir(String buffer) throws InterruptedException {
        while (true) {
            if (socket == null) {
                conectar(mCubiculosAuto);
            }
            if ("OK".equals(enviarLinea("PEDIR:" + buffer))) {
                return;
            }
            Thread.sleep(150);
        }
    }

    // SOLTAR es de mejor esfuerzo: si la conexión cayó, el maestro ya liberó el recurso.
    public void soltar(String buffer) {
        if (socket != null) {
            enviarLinea("SOLTAR:" + buffer);
        }
    }

    public String estadoMaestro() {
        String r = enviarLinea("ESTADO");
        return r != null ? r : "(maestro no disponible)";
    }

    public int getIdPiso() {
        return idPiso;
    }

    // Un solo comando en tránsito por conexión; null si el socket está muerto.
    private synchronized String enviarLinea(String comando) {
        if (socket == null) {
            return null;
        }
        try {
            out.println(comando);
            String respuesta = in.readLine();
            if (respuesta == null) {
                cerrar();
            }
            return respuesta;
        } catch (IOException e) {
            cerrar();
            return null;
        }
    }

    private void cerrar() {
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        socket = null;
        idPiso = -1;
    }
}
