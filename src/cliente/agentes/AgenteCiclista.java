package cliente.agentes;

import cliente.ClienteMaestroTCP;
import cliente.RecursosPiso;
import modelo.*;

public class AgenteCiclista extends AgentePiso {

    public AgenteCiclista(String id, RecursosPiso recursos, ConfiguracionSimulador config, ClienteMaestroTCP maestro) {
        super(id, "Ciclista", recursos, config, maestro);
    }

    @Override
    public void run() {
        try {
            // 1. Entrada por caseta vehicular (compartida con autos)
            estadoActual = EstadoBPMN.ESPERANDO_CASETA_ENTRADA;
            maestro.pedir("CV");
            estadoActual = EstadoBPMN.CRUZANDO_CASETA_ENTRADA;
            Thread.sleep(config.timerCaseta);
            maestro.soltar("CV");

            // 2. Cruzar de forma adaptativa: prefiere la vía peatonal si está abierta
            cruzarAdaptativo();

            // 3. Estacionar en Cb (exclusivo de bici, sin Mz ni Ap)
            estadoActual = EstadoBPMN.ESTACIONANDO_BICI;
            recursos.cbCubiculosBici.entrar();
            Thread.sleep(config.timerEstacionar);

            estadoActual = EstadoBPMN.BICICLETA_ESTACIONADA;
            Thread.sleep(config.timerEstanciaUniversidad / 2);
            estadoActual = EstadoBPMN.PERMANECIENDO_UNIVERSIDAD;
            Thread.sleep(config.timerEstanciaUniversidad / 2);

            // 4. Salida: liberar Cb y cruzar de regreso
            estadoActual = EstadoBPMN.SALIENDO_BICI;
            recursos.cbCubiculosBici.salir();
            cruzarAdaptativo();

            // 5. Caseta de salida
            estadoActual = EstadoBPMN.ESPERANDO_CASETA_SALIDA;
            maestro.pedir("CS");
            estadoActual = EstadoBPMN.CRUZANDO_CASETA_SALIDA;
            Thread.sleep(config.timerCaseta);
            maestro.soltar("CS");

            estadoActual = EstadoBPMN.FINALIZADO;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Elige en tiempo real cómo cruzar: como peatón si la vía peatonal está
    // abierta; si no, toma la calle Q y cruza como vehículo.
    private void cruzarAdaptativo() throws InterruptedException {
        if (recursos.cruceX.intentarEntrarPeaton()) {
            estadoActual = EstadoBPMN.CRUZANDO_COMO_PEATON;
            Thread.sleep(config.timerCruce);
            recursos.cruceX.salirPeaton();
            return;
        }

        recursos.qCallePiso.entrar();
        try {
            if (recursos.cruceX.intentarEntrarAuto()) {
                estadoActual = EstadoBPMN.CRUZANDO_COMO_VEHICULO;
            } else {
                estadoActual = EstadoBPMN.ESPERANDO_CRUCE;
                recursos.cruceX.entrarAuto();
                estadoActual = EstadoBPMN.CRUZANDO_COMO_VEHICULO;
            }
            Thread.sleep(config.timerCruce);
            recursos.cruceX.salirAuto();
        } finally {
            recursos.qCallePiso.salir();
        }
    }
}
