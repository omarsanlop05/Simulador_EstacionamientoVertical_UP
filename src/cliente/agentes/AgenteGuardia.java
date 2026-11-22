package cliente.agentes;

import cliente.ClienteMaestroTCP;
import cliente.RecursosPiso;
import modelo.*;

public class AgenteGuardia extends AgentePiso {
    public AgenteGuardia(String id, RecursosPiso recursos, ConfiguracionSimulador config, ClienteMaestroTCP maestro) {
        super(id, "Guardia", recursos, config, maestro);
    }

    @Override
    public void run() {
        try {
            for (int turno = 0; turno < 2; turno++) {
                // Toma cono Cn (local) y consulta cupo Ap al maestro
                estadoActual = EstadoBPMN.ELIGIENDO_PISO;
                recursos.cnConosGuardia.entrar();
                maestro.pedir("AP");

                // Bloquea cubículo Cu
                estadoActual = EstadoBPMN.COLOCANDO_CONO;
                recursos.cuCubiculosAuto.entrar();

                estadoActual = EstadoBPMN.MANTENIENDO_CONO;
                Thread.sleep(config.timerConoGuardia);

                // Retira cono: Cu' -> Ap' -> Cn'
                estadoActual = EstadoBPMN.RETIRANDO_CONO;
                recursos.cuCubiculosAuto.salir();
                maestro.soltar("AP");
                recursos.cnConosGuardia.salir();

                Thread.sleep(500);
            }
            estadoActual = EstadoBPMN.FINALIZADO;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
