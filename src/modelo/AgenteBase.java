package modelo;

public abstract class AgenteBase implements Runnable {
    protected final String id;
    protected final String tipo;
    protected volatile EstadoBPMN estadoActual = EstadoBPMN.LLEGADA;
    protected Thread hiloPropio;

    public AgenteBase(String id, String tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    public void setHiloPropio(Thread t) {
        this.hiloPropio = t;
    }

    public Thread.State getEstadoThread() {
        return (hiloPropio != null) ? hiloPropio.getState() : Thread.State.NEW;
    }

    public String getId() { return id; }
    public String getTipo() { return tipo; }
    public EstadoBPMN getEstadoActual() { return estadoActual; }
}
