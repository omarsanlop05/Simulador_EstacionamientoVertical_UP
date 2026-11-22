package servidor;

import modelo.BufferGenerico;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

// Tablero del sistema maestro: pisos conectados y ocupación de los recursos globales.
public class DashboardMaestro extends JFrame {
    private final RecursosMaestro recursosM;
    private final JTextArea txtResumen = new JTextArea();

    public DashboardMaestro(RecursosMaestro recursosM, int puerto) {
        this.recursosM = recursosM;

        setTitle("Sistema Maestro - Estacionamiento Vertical UP (puerto " + puerto + ")");
        setSize(560, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        txtResumen.setEditable(false);
        txtResumen.setFont(new Font("Monospaced", Font.PLAIN, 13));
        add(new JScrollPane(txtResumen));

        new Timer(500, e -> refrescar()).start();
    }

    private void refrescar() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SISTEMA MAESTRO ===\n");
        sb.append("Pisos conectados: ").append(recursosM.getPisosRegistrados()).append("\n\n");

        sb.append("=== RECURSOS GLOBALES (BUFFER 1) ===\n");
        sb.append(linea(recursosM.cvCasetaEntrada));
        sb.append(linea(recursosM.csCasetaSalida));
        sb.append(linea(recursosM.cpCasetaPeatonal));
        sb.append(linea(recursosM.ecCabinaElevador));

        sb.append("\n=== CUPO DE ADMISIÓN POR PISO (Ap) ===\n");
        for (Map.Entry<Integer, BufferGenerico> e : recursosM.getCupoPorPiso().entrySet()) {
            sb.append(linea(e.getValue()));
        }
        if (recursosM.getCupoPorPiso().isEmpty()) {
            sb.append("• Sin pisos registrados aún.\n");
        }

        txtResumen.setText(sb.toString());
    }

    private String linea(BufferGenerico b) {
        return String.format("• %s: %d/%d ocupados.%n", b.getNombre(), b.getOcupados(), b.getCapacidadM());
    }
}
