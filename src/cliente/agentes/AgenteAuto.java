package cliente.agentes;

import cliente.ClienteMaestroTCP;
import cliente.RecursosPiso;
import modelo.*;

public class AgenteAuto extends AgentePiso {
    public AgenteAuto(String id, RecursosPiso recursos, ConfiguracionSimulador config, ClienteMaestroTCP maestro) {
        super(id, "Auto", recursos, config, maestro);
    }

    @Override
    public void run() {
        try {
            // 1. Caseta entrada (Cv -> Cv'): recurso del maestro
            estadoActual = EstadoBPMN.ESPERANDO_CASETA_ENTRADA;
            maestro.pedir("CV");
            estadoActual = EstadoBPMN.CRUZANDO_CASETA_ENTRADA;
            Thread.sleep(config.timerCaseta);
            maestro.soltar("CV");

            // 2. Consultar admisión del piso al maestro (Ap)
            estadoActual = EstadoBPMN.CONSULTANDO_CUPO_TCP;
            maestro.pedir("AP");

            // 3. Entrar a calle Q y pasar cruce peatonal Xv (buffers del piso)
            estadoActual = EstadoBPMN.CIRCULANDO_CALLE;
            recursos.qCallePiso.entrar();
            Thread.sleep(config.timerCirculacion);

            estadoActual = EstadoBPMN.ESPERANDO_CRUCE;
            recursos.cruceX.entrarAuto(); // Espera si hay peatones cruzando
            estadoActual = EstadoBPMN.CRUZANDO_PASO_PEATONAL;
            Thread.sleep(config.timerCruce);
            recursos.cruceX.salirAuto();

            // 4. Reservar cubículo Cu y maniobrar en Mz
            estadoActual = EstadoBPMN.MANIOBRANDO_CUBICULO;
            recursos.cuCubiculosAuto.entrar();
            recursos.mzZonaManiobra.entrar();
            Thread.sleep(config.timerEstacionar);
            recursos.mzZonaManiobra.salir(); // Mz'
            recursos.qCallePiso.salir();     // Q'

            // 5. Estacionado / Permanecer en universidad
            estadoActual = EstadoBPMN.ESTACIONADO_EN_CUBICULO;
            Thread.sleep(config.timerEstanciaUniversidad / 2);
            estadoActual = EstadoBPMN.PERMANECIENDO_UNIVERSIDAD;
            Thread.sleep(config.timerEstanciaUniversidad / 2);

            // 6. Retiro: tomar Mz y Q, liberar Cu' y Mz', notificar Ap' al maestro
            estadoActual = EstadoBPMN.SALIENDO_CUBICULO;
            recursos.mzZonaManiobra.entrar();
            recursos.qCallePiso.entrar();
            Thread.sleep(config.timerEstacionar);
            recursos.cuCubiculosAuto.salir(); // Cu'
            recursos.mzZonaManiobra.salir();  // Mz'
            maestro.soltar("AP");             // Ap'

            // 7. Circular hacia caseta de salida Cs -> Cs' -> Q'
            estadoActual = EstadoBPMN.ESPERANDO_CASETA_SALIDA;
            maestro.pedir("CS");
            estadoActual = EstadoBPMN.CRUZANDO_CASETA_SALIDA;
            Thread.sleep(config.timerCaseta);
            maestro.soltar("CS");
            recursos.qCallePiso.salir();

            estadoActual = EstadoBPMN.FINALIZADO;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
