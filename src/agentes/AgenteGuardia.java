package agentes;

import modelo.*;

public class AgenteGuardia extends AgenteBase {
    public AgenteGuardia(String id, GestorRecursos recursos, ConfiguracionSimulador config) {
        super(id, "Guardia", recursos, config);
    }

    @Override
    public void run() {
        try {
            for (int turno = 0; turno < 2; turno++) {
                // Toma cono Cn y cupo Ap
                estadoActual = EstadoBPMN.ELIGIENDO_PISO;
                recursos.cnConosGuardia.entrar();
                recursos.semaforoCupoAp.acquire();

                // Bloquea cubículo Cu
                estadoActual = EstadoBPMN.COLOCANDO_CONO;
                recursos.cuCubiculosAuto.entrar();

                estadoActual = EstadoBPMN.MANTENIENDO_CONO;
                Thread.sleep(config.timerConoGuardia);

                // Retira cono: Cu' -> Ap' -> Cn'
                estadoActual = EstadoBPMN.RETIRANDO_CONO;
                recursos.cuCubiculosAuto.salir();
                recursos.semaforoCupoAp.release();
                recursos.cnConosGuardia.salir();

                Thread.sleep(500);
            }
            estadoActual = EstadoBPMN.FINALIZADO;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}