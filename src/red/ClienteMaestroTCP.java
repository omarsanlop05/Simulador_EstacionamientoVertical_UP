package red;

import java.io.*;
import java.net.Socket;

public class ClienteMaestroTCP {
    private final String host;
    private final int puerto;

    public ClienteMaestroTCP(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    public synchronized String enviarComando(String comando) {
        try (Socket socket = new Socket(host, puerto);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println(comando);
            return in.readLine();
        } catch (IOException e) {
            return "LOCAL_FALLBACK";
        }
    }
}