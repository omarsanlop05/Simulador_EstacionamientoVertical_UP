package cliente.vista;

import modelo.ConfiguracionSimulador;
import javax.swing.*;
import java.awt.*;

public class VentanaConfiguracion extends JFrame {
    private final ConfiguracionSimulador cfg;

    private JSpinner spAutos, spPeatones, spCiclistas, spGuardias;
    private JSpinner spCu, spCb, spQ, spX, spEw, spEc, spCn;
    private JSpinner spTCaseta, spTCirc, spTEstac, spTUniv, spTCono, spTCruce;

    public VentanaConfiguracion() {
        this(new ConfiguracionSimulador());
    }

    public VentanaConfiguracion(ConfiguracionSimulador cfg) {
        this.cfg = cfg;
        setTitle("b) Configuración Piso - Estacionamiento Vertical UP");
        setSize(680, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelCentro = new JPanel(new GridLayout(1, 2, 12, 10));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Izquierdo: Agentes y Buffers (m de n)
        JPanel pBuffers = new JPanel(new GridLayout(12, 2, 5, 5));
        pBuffers.setBorder(BorderFactory.createTitledBorder("Agentes y Buffers (m <= n)"));

        spAutos = crearSpinner(cfg.numAutos, 1, ConfiguracionSimulador.MAX_AUTOS);
        spPeatones = crearSpinner(cfg.numPeatones, 1, ConfiguracionSimulador.MAX_PEATONES);
        spCiclistas = crearSpinner(cfg.numCiclistas, 0, ConfiguracionSimulador.MAX_CICLISTAS);
        spGuardias = crearSpinner(cfg.numGuardias, 1, ConfiguracionSimulador.MAX_GUARDIAS);

        spCu = crearSpinner(cfg.mCubiculosAuto, 1, ConfiguracionSimulador.MAX_CUBICULOS_AUTO_PISO);
        spCb = crearSpinner(cfg.mCubiculosBici, 1, ConfiguracionSimulador.MAX_CUBICULOS_BICI_PISO);
        spQ  = crearSpinner(cfg.mCallePiso, 1, ConfiguracionSimulador.MAX_CALLE_PISO);
        spX  = crearSpinner(cfg.mCrucePeatonal, 1, ConfiguracionSimulador.MAX_CRUCE_PEATONAL);
        spEw = crearSpinner(cfg.mEsperaElevador, 1, ConfiguracionSimulador.MAX_ESPERA_ELEVADOR);
        spEc = crearSpinner(cfg.mCabinaElevador, 1, ConfiguracionSimulador.MAX_CABINA_ELEVADOR);
        spCn = crearSpinner(cfg.mConos, 1, ConfiguracionSimulador.MAX_CONOS);

        pBuffers.add(new JLabel("Autos (m de " + ConfiguracionSimulador.MAX_AUTOS + "):")); pBuffers.add(spAutos);
        pBuffers.add(new JLabel("Peatones (m de " + ConfiguracionSimulador.MAX_PEATONES + "):")); pBuffers.add(spPeatones);
        pBuffers.add(new JLabel("Ciclistas (m de " + ConfiguracionSimulador.MAX_CICLISTAS + "):")); pBuffers.add(spCiclistas);
        pBuffers.add(new JLabel("Guardias (m de " + ConfiguracionSimulador.MAX_GUARDIAS + "):")); pBuffers.add(spGuardias);
        pBuffers.add(new JLabel("Caseta Cv / Cs / Cp (Buffer 1):")); pBuffers.add(new JLabel("1 de 1 (Fijo)"));
        pBuffers.add(new JLabel("Cubículos Auto Cu (m de " + ConfiguracionSimulador.MAX_CUBICULOS_AUTO_PISO + "):")); pBuffers.add(spCu);
        pBuffers.add(new JLabel("Cubículos Bici Cb (m de " + ConfiguracionSimulador.MAX_CUBICULOS_BICI_PISO + "):")); pBuffers.add(spCb);
        pBuffers.add(new JLabel("Calle Piso Q (m de " + ConfiguracionSimulador.MAX_CALLE_PISO + "):")); pBuffers.add(spQ);
        pBuffers.add(new JLabel("Cruce Peatonal Xp (m de " + ConfiguracionSimulador.MAX_CRUCE_PEATONAL + "):")); pBuffers.add(spX);
        pBuffers.add(new JLabel("Espera Elevador Ew (m de " + ConfiguracionSimulador.MAX_ESPERA_ELEVADOR + "):")); pBuffers.add(spEw);
        pBuffers.add(new JLabel("Cabina Elevador Ec (m de " + ConfiguracionSimulador.MAX_CABINA_ELEVADOR + "):")); pBuffers.add(spEc);
        pBuffers.add(new JLabel("Conos Guardia Cn (m de " + ConfiguracionSimulador.MAX_CONOS + "):")); pBuffers.add(spCn);

        // Panel Derecho: Timers
        JPanel pTimers = new JPanel(new GridLayout(7, 2, 5, 8));
        pTimers.setBorder(BorderFactory.createTitledBorder("Timers de Simulación (ms)"));

        spTCaseta = crearSpinner(cfg.timerCaseta, 100, 10000);
        spTCirc   = crearSpinner(cfg.timerCirculacion, 100, 10000);
        spTEstac  = crearSpinner(cfg.timerEstacionar, 100, 10000);
        spTUniv   = crearSpinner(cfg.timerEstanciaUniversidad, 500, 20000);
        spTCono   = crearSpinner(cfg.timerConoGuardia, 200, 15000);
        spTCruce  = crearSpinner(cfg.timerCruce, 100, 10000);

        pTimers.add(new JLabel("Timer Caseta (ms):")); pTimers.add(spTCaseta);
        pTimers.add(new JLabel("Timer Circulación (ms):")); pTimers.add(spTCirc);
        pTimers.add(new JLabel("Timer Estacionar (ms):")); pTimers.add(spTEstac);
        pTimers.add(new JLabel("Timer Estancia Univ (ms):")); pTimers.add(spTUniv);
        pTimers.add(new JLabel("Timer Cono Guardia (ms):")); pTimers.add(spTCono);
        pTimers.add(new JLabel("Timer Cruce Peatonal (ms):")); pTimers.add(spTCruce);

        panelCentro.add(pBuffers);
        panelCentro.add(pTimers);
        add(panelCentro, BorderLayout.CENTER);

        JButton btnIniciar = new JButton("Guardar Configuración e Iniciar Tablero de Simulación");
        btnIniciar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnIniciar.addActionListener(e -> lanzarSimulador());
        add(btnIniciar, BorderLayout.SOUTH);
    }

    private JSpinner crearSpinner(int val, int min, int max) {
        return new JSpinner(new SpinnerNumberModel(val, min, max, 1));
    }

    private void lanzarSimulador() {
        cfg.numAutos = (int) spAutos.getValue();
        cfg.numPeatones = (int) spPeatones.getValue();
        cfg.numCiclistas = (int) spCiclistas.getValue();
        cfg.numGuardias = (int) spGuardias.getValue();

        cfg.mCubiculosAuto = (int) spCu.getValue();
        cfg.mCubiculosBici = (int) spCb.getValue();
        cfg.mCallePiso = (int) spQ.getValue();
        cfg.mCrucePeatonal = (int) spX.getValue();
        cfg.mEsperaElevador = (int) spEw.getValue();
        cfg.mCabinaElevador = (int) spEc.getValue();
        cfg.mConos = (int) spCn.getValue();

        cfg.timerCaseta = (int) spTCaseta.getValue();
        cfg.timerCirculacion = (int) spTCirc.getValue();
        cfg.timerEstacionar = (int) spTEstac.getValue();
        cfg.timerEstanciaUniversidad = (int) spTUniv.getValue();
        cfg.timerConoGuardia = (int) spTCono.getValue();
        cfg.timerCruce = (int) spTCruce.getValue();

        dispose();
        new DashboardPrincipal(cfg).setVisible(true);
    }

    // Punto de entrada del cliente (un piso). Uso: [hostMaestro] [puertoMaestro]
    public static void main(String[] args) {
        ConfiguracionSimulador cfg = new ConfiguracionSimulador();
        if (args.length >= 1) cfg.hostMaestro = args[0];
        if (args.length >= 2) cfg.puertoMaestro = Integer.parseInt(args[1]);
        SwingUtilities.invokeLater(() -> new VentanaConfiguracion(cfg).setVisible(true));
    }
}