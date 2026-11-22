package modelo;

public class BufferGenerico {
    private final String nombre;
    private final int capacidadM;
    private int ocupados = 0;
    private int esperando = 0;

    public BufferGenerico(String nombre, int capacidadM) {
        this.nombre = nombre;
        this.capacidadM = capacidadM;
    }

    // Operación GUARDAR (evento azul BPMN)
    public synchronized void entrar() throws InterruptedException {
        esperando++;
        try {
            while (ocupados >= capacidadM) {
                wait();
            }
            ocupados++;
        } finally {
            esperando--;
        }
    }

    // Intento sin bloqueo (consulta de cupo remota vía TCP)
    public synchronized boolean intentarEntrar() {
        if (ocupados >= capacidadM) {
            return false;
        }
        ocupados++;
        return true;
    }

    // Operación SACAR (evento naranja con apóstrofe BPMN)
    public synchronized void salir() {
        if (ocupados > 0) {
            ocupados--;
            notifyAll();
        }
    }

    public synchronized int getOcupados() { return ocupados; }
    public synchronized int getEsperando() { return esperando; }
    public int getCapacidadM() { return capacidadM; }
    public String getNombre() { return nombre; }
}