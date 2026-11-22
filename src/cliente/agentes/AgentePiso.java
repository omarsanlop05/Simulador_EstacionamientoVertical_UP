package cliente.agentes;

import cliente.ClienteMaestroTCP;
import cliente.RecursosPiso;
import modelo.AgenteBase;
import modelo.ConfiguracionSimulador;

// Base de los agentes que corren dentro de un piso (cliente):
// usan los buffers locales del piso y consultan al maestro por TCP.
public abstract class AgentePiso extends AgenteBase {
    protected final RecursosPiso recursos;
    protected final ConfiguracionSimulador config;
    protected final ClienteMaestroTCP maestro;

    public AgentePiso(String id, String tipo, RecursosPiso recursos,
                      ConfiguracionSimulador config, ClienteMaestroTCP maestro) {
        super(id, tipo);
        this.recursos = recursos;
        this.config = config;
        this.maestro = maestro;
    }
}
