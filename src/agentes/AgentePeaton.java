package agentes;

import modelo.*;

public class AgentePeaton extends AgenteBase {
    public AgentePeaton(String id, GestorRecursos recursos, ConfiguracionSimulador config) {
        super(id, "Peatón", recursos, config);
    }

    @Override
    public void run() {
        try {
            // 1. Caseta peatonal (Cp -> Cp')
            estadoActual = EstadoBPMN.ESPERANDO_CASETA_PEATONAL;
            recursos.cpCasetaPeatonal.entrar();
            Thread.sleep(config.timerCaseta / 2);
            recursos.cpCasetaPeatonal.salir();

            // 2. Cruzar calle (Xp -> Xp') interactuando con autos en el mismo monitor
            estadoActual = EstadoBPMN.CRUZANDO_CALLE_PEATON;
            recursos.cruceX.entrarPeaton();
            Thread.sleep(config.timerCruce);
            recursos.cruceX.salirPeaton();

            // 3. Zona espera elevador (Ew) y cabina (Ec)
            estadoActual = EstadoBPMN.ESPERANDO_ELEVADOR;
            recursos.ewEsperaElevador.entrar();
            Thread.sleep(400);
            recursos.ecCabinaElevador.entrar();
            recursos.ewEsperaElevador.salir(); // Ew'

            estadoActual = EstadoBPMN.EN_CABINA_ELEVADOR;
            Thread.sleep(config.timerCirculacion);
            recursos.ecCabinaElevador.salir(); // Ec'

            // 4. Permanecer en universidad y regresar
            estadoActual = EstadoBPMN.PERMANECIENDO_UNIVERSIDAD;
            Thread.sleep(config.timerEstanciaUniversidad);

            // Cruce de regreso
            estadoActual = EstadoBPMN.CRUZANDO_CALLE_PEATON;
            recursos.cruceX.entrarPeaton();
            Thread.sleep(config.timerCruce);
            recursos.cruceX.salirPeaton();

            estadoActual = EstadoBPMN.FINALIZADO;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}