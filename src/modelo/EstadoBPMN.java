package modelo;

public enum EstadoBPMN {
    // Estados generales y Auto
    LLEGADA("Llegada al sistema"),
    ESPERANDO_CASETA_ENTRADA("Esperando caseta entrada (Cv)"),
    CRUZANDO_CASETA_ENTRADA("Cruzando caseta entrada"),
    CONSULTANDO_CUPO_TCP("Consultando cupo piso (TCP / Ap)"),
    CIRCULANDO_CALLE("Circulando en calle (Q)"),
    ESPERANDO_CRUCE("Esperando cruce peatonal (Xv)"),
    CRUZANDO_PASO_PEATONAL("Cruzando paso peatonal"),
    MANIOBRANDO_CUBICULO("Maniobrando en zona Mz"),
    ESTACIONADO_EN_CUBICULO("Estacionado en cubículo (Cu)"),
    PERMANECIENDO_UNIVERSIDAD("Permaneciendo en universidad"),
    SALIENDO_CUBICULO("Saliendo del cubículo"),
    ESPERANDO_CASETA_SALIDA("Esperando caseta salida (Cs)"),
    CRUZANDO_CASETA_SALIDA("Cruzando caseta salida"),
    ESCUCHANDO_PISOS("Escuchando conexiones de pisos (TCP)"),
    FINALIZADO("Visita finalizada"),

    // Estados Peatón
    ESPERANDO_CASETA_PEATONAL("Esperando caseta peatonal (Cp)"),
    CRUZANDO_CALLE_PEATON("Cruzando calle (Xp)"),
    ESPERANDO_ELEVADOR("En zona espera elevador (Ew)"),
    EN_CABINA_ELEVADOR("Viajando en cabina (Ec)"),

    // Estados Guardia
    ELIGIENDO_PISO("Eligiendo piso / tomando cono (Cn)"),
    COLOCANDO_CONO("Colocando cono en cubículo (Cu)"),
    MANTENIENDO_CONO("Manteniendo cubículo bloqueado"),
    RETIRANDO_CONO("Retirando cono"),

    // Estados Ciclista
    CRUZANDO_COMO_PEATON("Cruzando como peatón (Xp) - bicicleta"),
    CRUZANDO_COMO_VEHICULO("Cruzando como vehículo (Xv) - bicicleta"),
    ESTACIONANDO_BICI("Estacionando bicicleta (Cb)"),
    BICICLETA_ESTACIONADA("Bicicleta estacionada (Cb)"),
    SALIENDO_BICI("Retirando bicicleta (Cb')");

    private final String descripcion;

    EstadoBPMN(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}