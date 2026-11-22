package servidor;

import modelo.ConfiguracionSimulador;

import javax.swing.*;
import java.awt.*;

// Punto de entrada del sistema maestro (host).
// Uso: java servidor.MainServidor [puerto]
public class MainServidor {
    public static void main(String[] args) {
        ConfiguracionSimulador cfg = new ConfiguracionSimulador();
        if (args.length >= 1) {
            cfg.puertoMaestro = Integer.parseInt(args[0]);
        }

        RecursosMaestro recursosM = new RecursosMaestro(cfg);
        ServidorMaestroTCP servidor = new ServidorMaestroTCP("Maestro-TCP", cfg.puertoMaestro, recursosM);
        new Thread(servidor, "ServidorMaestro").start();

        if (!GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeLater(() -> new DashboardMaestro(recursosM, cfg.puertoMaestro).setVisible(true));
        } else {
            System.out.println("Maestro escuchando en puerto " + cfg.puertoMaestro);
        }
    }
}
