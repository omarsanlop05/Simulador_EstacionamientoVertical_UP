package servidor;

import modelo.BufferGenerico;
import modelo.ConfiguracionSimulador;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Recursos globales del estacionamiento, administrados por el sistema maestro:
// casetas de acceso, la cabina del elevador y el cupo de admisión (Ap) por piso.
public class RecursosMaestro {
    public final BufferGenerico cvCasetaEntrada;
    public final BufferGenerico csCasetaSalida;
    public final BufferGenerico cpCasetaPeatonal;
    public final BufferGenerico ecCabinaElevador;

    private final Map<Integer, BufferGenerico> cupoPorPiso = new ConcurrentHashMap<>();
    private final AtomicInteger contadorPisos = new AtomicInteger();

    public RecursosMaestro(ConfiguracionSimulador cfg) {
        cvCasetaEntrada = new BufferGenerico("Cv (Caseta Entrada)", 1);
        csCasetaSalida = new BufferGenerico("Cs (Caseta Salida)", 1);
        cpCasetaPeatonal = new BufferGenerico("Cp (Caseta Peatonal)", 1);
        ecCabinaElevador = new BufferGenerico("Ec (Cabina Elevador)", cfg.mCabinaElevador);
    }

    public int registrarPiso(int mCubiculosAuto) {
        int id = contadorPisos.incrementAndGet();
        cupoPorPiso.put(id, new BufferGenerico("Ap (Cupo Autos Piso-" + id + ")", mCubiculosAuto));
        return id;
    }

    public boolean intentarEntrar(int piso, String clave) {
        BufferGenerico b = buffer(piso, clave);
        return b != null && b.intentarEntrar();
    }

    public void salir(int piso, String clave) {
        BufferGenerico b = buffer(piso, clave);
        if (b != null) {
            b.salir();
        }
    }

    public void liberarTodo(int piso, Set<String> sostenidos) {
        for (String clave : sostenidos) {
            salir(piso, clave);
        }
        sostenidos.clear();
    }

    private BufferGenerico buffer(int piso, String clave) {
        return switch (clave) {
            case "CV" -> cvCasetaEntrada;
            case "CS" -> csCasetaSalida;
            case "CP" -> cpCasetaPeatonal;
            case "EC" -> ecCabinaElevador;
            case "AP" -> cupoPorPiso.get(piso);
            default -> null;
        };
    }

    // Resumen en una sola línea para la consulta ESTADO del protocolo TCP.
    public String resumenLinea() {
        StringBuilder sb = new StringBuilder();
        sb.append(linea(cvCasetaEntrada)).append(linea(csCasetaSalida))
                .append(linea(cpCasetaPeatonal)).append(linea(ecCabinaElevador));
        cupoPorPiso.forEach((id, ap) -> sb.append(linea(ap)));
        return sb.toString().trim();
    }

    private String linea(BufferGenerico b) {
        return String.format("%s:%d/%d  ", b.getNombre(), b.getOcupados(), b.getCapacidadM());
    }

    public Map<Integer, BufferGenerico> getCupoPorPiso() {
        return cupoPorPiso;
    }

    public int getPisosRegistrados() {
        return contadorPisos.get();
    }
}
