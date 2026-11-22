package cliente.agentes;

import cliente.ClienteMaestroTCP;
import modelo.*;

public class AgenteAuto extends AgenteBase {
    private final ClienteMaestroTCP clienteTCP;

    public AgenteAuto(String id, GestorRecursos recursos, ConfiguracionSimulador config, ClienteMaestroTCP clienteTCP) {
        super(id, "Auto", recursos, config);
        this.clienteTCP = clienteTCP;
    }

    @Override
    public void run() {
        try {
            // 1. Caseta entrada (Cv -> Cv')
            estadoActual = EstadoBPMN.ESPERANDO_CASETA_ENTRADA;
            recursos.cvCasetaEntrada.entrar();
            estadoActual = EstadoBPMN.CRUZANDO_CASETA_ENTRADA;
            Thread.sleep(config.timerCaseta);
            recursos.cvCasetaEntrada.salir();

            // 2. Consultar admisión al piso remoto vía TCP (Ap)
            estadoActual = EstadoBPMN.CONSULTANDO_CUPO_TCP;
            recursos.semaforoCupoAp.acquire(); // Adquiere cupo Ap
            clienteTCP.enviarComando("RESERVAR_AP:" + id);

            // 3. Entrar a calle Q y pasar cruce peatonal Xv
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

            // 6. Retiro: tomar Mz y Q, liberar Cu' y Mz', notificar Ap' por TCP
            estadoActual = EstadoBPMN.SALIENDO_CUBICULO;
            recursos.mzZonaManiobra.entrar();
            recursos.qCallePiso.entrar();
            Thread.sleep(config.timerEstacionar);
            recursos.cuCubiculosAuto.salir(); // Cu'
            recursos.mzZonaManiobra.salir();  // Mz'
            recursos.semaforoCupoAp.release(); // Ap'
            clienteTCP.enviarComando("LIBERAR_AP:" + id);

            // 7. Circular hacia caseta de salida Cs -> Cs' -> Q'
            estadoActual = EstadoBPMN.ESPERANDO_CASETA_SALIDA;
            recursos.csCasetaSalida.entrar();
            estadoActual = EstadoBPMN.CRUZANDO_CASETA_SALIDA;
            Thread.sleep(config.timerCaseta);
            recursos.csCasetaSalida.salir();
            recursos.qCallePiso.salir();

            estadoActual = EstadoBPMN.FINALIZADO;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}