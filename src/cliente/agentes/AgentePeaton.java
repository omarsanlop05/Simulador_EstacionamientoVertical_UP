package cliente.agentes;

import cliente.ClienteMaestroTCP;
import cliente.RecursosPiso;
import modelo.*;

public class AgentePeaton extends AgentePiso {
    public AgentePeaton(String id, RecursosPiso recursos, ConfiguracionSimulador config, ClienteMaestroTCP maestro) {
        super(id, "Peatón", recursos, config, maestro);
    }

    @Override
    public void run() {
        try {
            // 1. Caseta peatonal (Cp -> Cp'): recurso del maestro
            estadoActual = EstadoBPMN.ESPERANDO_CASETA_PEATONAL;
            maestro.pedir("CP");
            Thread.sleep(config.timerCaseta / 2);
            maestro.soltar("CP");

            // 2. Cruzar calle (Xp -> Xp') interactuando con autos en el mismo monitor del piso
            estadoActual = EstadoBPMN.CRUZANDO_CALLE_PEATON;
            recursos.cruceX.entrarPeaton();
            Thread.sleep(config.timerCruce);
            recursos.cruceX.salirPeaton();

            // 3. Zona espera elevador (Ew local) y cabina (Ec en el maestro)
            estadoActual = EstadoBPMN.ESPERANDO_ELEVADOR;
            recursos.ewEsperaElevador.entrar();
            Thread.sleep(400);
            maestro.pedir("EC");
            recursos.ewEsperaElevador.salir(); // Ew'

            estadoActual = EstadoBPMN.EN_CABINA_ELEVADOR;
            Thread.sleep(config.timerCirculacion);
            maestro.soltar("EC"); // Ec'

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
