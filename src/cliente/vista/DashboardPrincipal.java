package cliente.vista;

import cliente.ClienteMaestroTCP;
import cliente.RecursosPiso;
import cliente.agentes.*;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardPrincipal extends JFrame {
    private final ConfiguracionSimulador cfg;
    private final RecursosPiso recursos;
    private final ClienteMaestroTCP clienteTCP;
    private final List<AgenteBase> todosLosAgentes = new ArrayList<>();

    // Labels Rubro C (Estados de Threads)
    private JLabel lblRunnable, lblTimedWaiting, lblWaiting, lblBlocked, lblTerminated;

    // Area de texto Rubro D (Conteo agentes, buffers y zonas críticas)
    private JTextArea txtResumenGeneral;

    // Tabla Rubro F (Estados BPMN por agente)
    private DefaultTableModel modeloTablaBPMN;

    public DashboardPrincipal(ConfiguracionSimulador cfg) {
        this.cfg = cfg;
        this.recursos = new RecursosPiso(cfg);
        this.clienteTCP = new ClienteMaestroTCP(cfg.hostMaestro, cfg.puertoMaestro);

        setTitle("Tablero del Piso - Simulador Estacionamiento Vertical UP");
        setSize(1100, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        construirInterfaz();
        iniciarAgentes();

        // Timer Swing cada 250 ms para refrescar métricas en tiempo real sin bloquear hilos
        new Timer(250, e -> actualizarTableros()).start();
    }

    private void construirInterfaz() {
        // --- PANEL SUPERIOR: RUBRO C (Estados de Threads) ---
        JPanel panelThreads = new JPanel(new GridLayout(1, 5, 10, 10));
        panelThreads.setBorder(BorderFactory.createTitledBorder("c) Estado de los Threads en Tiempo Real"));
        lblRunnable = crearTarjetaEstado("RUNNABLE: 0", new Color(220, 255, 220));
        lblTimedWaiting = crearTarjetaEstado("TIMED_WAITING: 0", new Color(220, 240, 255));
        lblWaiting = crearTarjetaEstado("WAITING: 0", new Color(255, 250, 205));
        lblBlocked = crearTarjetaEstado("BLOCKED: 0", new Color(255, 220, 220));
        lblTerminated = crearTarjetaEstado("TERMINATED: 0", new Color(235, 235, 235));

        panelThreads.add(lblRunnable);
        panelThreads.add(lblTimedWaiting);
        panelThreads.add(lblWaiting);
        panelThreads.add(lblBlocked);
        panelThreads.add(lblTerminated);
        add(panelThreads, BorderLayout.NORTH);

        // --- PANEL CENTRAL DIVIDIDO: RUBRO D (Izquierda) y RUBRO F (Derecha) ---
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(460);

        // Rubro D: Resumen de agentes, buffers y secciones críticas
        txtResumenGeneral = new JTextArea();
        txtResumenGeneral.setEditable(false);
        txtResumenGeneral.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollD = new JScrollPane(txtResumenGeneral);
        scrollD.setBorder(BorderFactory.createTitledBorder("d) Conteo de Agentes, Buffers y Secciones Críticas"));
        split.setLeftComponent(scrollD);

        // Rubro F: Tabla de estados BPMN de cada agente
        String[] columnas = {"ID Agente", "Tipo", "Estado Thread", "Estado BPMN Actual"};
        modeloTablaBPMN = new DefaultTableModel(columnas, 0);
        JTable tablaBPMN = new JTable(modeloTablaBPMN);
        tablaBPMN.setRowHeight(24);
        JScrollPane scrollF = new JScrollPane(tablaBPMN);
        scrollF.setBorder(BorderFactory.createTitledBorder("f) Estados BPMN por Agente en Ejecución"));
        split.setRightComponent(scrollF);

        add(split, BorderLayout.CENTER);
    }

    private JLabel crearTarjetaEstado(String texto, Color bg) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lbl.setPreferredSize(new Dimension(150, 45));
        return lbl;
    }

    private void iniciarAgentes() {
        // 1. Registrar este piso en el sistema maestro (reintenta hasta conectar)
        try {
            clienteTCP.conectar(cfg.mCubiculosAuto);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        setTitle("Tablero del Piso-" + clienteTCP.getIdPiso() + " - Simulador Estacionamiento Vertical UP");

        // 2. Iniciar Agentes Auto
        for (int i = 1; i <= cfg.numAutos; i++) {
            registrarYArrancar(new AgenteAuto("Auto-" + i, recursos, cfg, clienteTCP));
        }

        // 3. Iniciar Agentes Peatón (Interactúan con Autos en el Cruce X)
        for (int i = 1; i <= cfg.numPeatones; i++) {
            registrarYArrancar(new AgentePeaton("Peatón-" + i, recursos, cfg, clienteTCP));
        }

        // 4. Iniciar Agentes Guardia (Interactúan con Autos compitiendo por Ap y Cu)
        for (int i = 1; i <= cfg.numGuardias; i++) {
            registrarYArrancar(new AgenteGuardia("Guardia-" + i, recursos, cfg, clienteTCP));
        }

        // 5. Iniciar Agentes Ciclista (usan Cb y cruzan de forma adaptativa)
        for (int i = 1; i <= cfg.numCiclistas; i++) {
            registrarYArrancar(new AgenteCiclista("Ciclista-" + i, recursos, cfg, clienteTCP));
        }
    }

    private void registrarYArrancar(AgenteBase agente) {
        Thread t = new Thread(agente, agente.getId());
        agente.setHiloPropio(t);
        todosLosAgentes.add(agente);
        modeloTablaBPMN.addRow(new Object[]{
                agente.getId(), agente.getTipo(), t.getState(), agente.getEstadoActual()
        });
        t.start();
    }

    private void actualizarTableros() {
        int runnable = 0, timedWaiting = 0, waiting = 0, blocked = 0, terminated = 0;

        for (int i = 0; i < todosLosAgentes.size(); i++) {
            AgenteBase ag = todosLosAgentes.get(i);
            Thread.State st = ag.getEstadoThread();

            switch (st) {
                case RUNNABLE -> runnable++;
                case TIMED_WAITING -> timedWaiting++;
                case WAITING -> waiting++;
                case BLOCKED -> blocked++;
                case TERMINATED -> terminated++;
                default -> {}
            }

            // Actualizar tabla BPMN (Rubro f)
            modeloTablaBPMN.setValueAt(st, i, 2);
            modeloTablaBPMN.setValueAt(ag.getEstadoActual().toString(), i, 3);
        }

        // Actualizar tarjetas Rubro C
        lblRunnable.setText("RUNNABLE: " + runnable);
        lblTimedWaiting.setText("TIMED_WAITING: " + timedWaiting);
        lblWaiting.setText("WAITING: " + waiting);
        lblBlocked.setText("BLOCKED: " + blocked);
        lblTerminated.setText("TERMINATED: " + terminated);

        // Actualizar texto Rubro D
        StringBuilder sb = new StringBuilder();
        sb.append("=== TOTAL DE AGENTES POR TIPO ===\n");
        sb.append(String.format("• Agente Auto: hay %d agentes.\n", cfg.numAutos));
        sb.append(String.format("• Agente Peatón: hay %d agentes.\n", cfg.numPeatones));
        sb.append(String.format("• Agente Ciclista: hay %d agentes.\n", cfg.numCiclistas));
        sb.append(String.format("• Agente Guardia: hay %d agentes.\n", cfg.numGuardias));
        sb.append(String.format("• Conectado al maestro %s:%d como Piso-%d.\n\n",
                cfg.hostMaestro, cfg.puertoMaestro, clienteTCP.getIdPiso()));

        sb.append("=== RECURSOS DEL MAESTRO (REMOTOS, TCP) ===\n");
        sb.append("  ").append(clienteTCP.estadoMaestro()).append("\n\n");

        sb.append("=== BUFFERS LOCALES m > 1 (ZONAS CRÍTICAS) ===\n");
        sb.append(formatearBuffer(recursos.cuCubiculosAuto));
        sb.append(formatearBuffer(recursos.cbCubiculosBici));
        sb.append(formatearBuffer(recursos.qCallePiso));
        sb.append(formatearBuffer(recursos.ewEsperaElevador));
        sb.append(formatearBuffer(recursos.cnConosGuardia));
        sb.append(String.format("• Hay %d agentes esperando por entrar a Cruce X.\n", recursos.cruceX.getEsperandoTotal()));
        sb.append(String.format("• Hay %d peatones y %d autos dentro de Cruce X.\n\n",
                recursos.cruceX.getPeatonesCruzando(), recursos.cruceX.getAutosCruzando()));

        sb.append("=== SECCIONES CRÍTICAS LOCALES (BUFFER 1) ===\n");
        sb.append(formatearZonaCritica(recursos.mzZonaManiobra));

        txtResumenGeneral.setText(sb.toString());
    }

    private String formatearBuffer(BufferGenerico b) {
        return String.format("• Hay %d esperando por entrar al Búfer %s.\n  Hay %d/%d agentes en el Búfer %s.\n",
                b.getEsperando(), b.getNombre(), b.getOcupados(), b.getCapacidadM(), b.getNombre());
    }

    private String formatearZonaCritica(BufferGenerico b) {
        return String.format("• Hay %d esperando por entrar a zona crítica %s (Ocupado: %d/1).\n",
                b.getEsperando(), b.getNombre(), b.getOcupados());
    }
}