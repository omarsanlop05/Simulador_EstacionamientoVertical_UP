package modelo;

public class ConfiguracionSimulador {
    // Capacidades máximas (n) - Constantes del sistema
    public static final int MAX_AUTOS = 50;
    public static final int MAX_PEATONES = 80;
    public static final int MAX_CICLISTAS = 30;
    public static final int MAX_GUARDIAS = 5;

    public static final int MAX_CUBICULOS_AUTO_PISO = 20; // n para Cu
    public static final int MAX_CUBICULOS_BICI_PISO = 15; // n para Cb
    public static final int MAX_CALLE_PISO = 10;          // n para Q
    public static final int MAX_CRUCE_PEATONAL = 8;       // n para Xp
    public static final int MAX_ESPERA_ELEVADOR = 12;     // n para Ew
    public static final int MAX_CABINA_ELEVADOR = 6;      // n para Ec
    public static final int MAX_CONOS = 5;                // n para Cn

    // Variables configurables por el usuario (m <= n)
    public int numAutos = 6;
    public int numPeatones = 6;
    public int numCiclistas = 2;
    public int numGuardias = 2;

    public int mCubiculosAuto = 4;    // m <= MAX_CUBICULOS_AUTO_PISO
    public int mCubiculosBici = 3;    // m <= MAX_CUBICULOS_BICI_PISO
    public int mCallePiso = 3;        // m <= MAX_CALLE_PISO
    public int mCrucePeatonal = 3;    // m <= MAX_CRUCE_PEATONAL
    public int mEsperaElevador = 5;   // m <= MAX_ESPERA_ELEVADOR
    public int mCabinaElevador = 4;   // m <= MAX_CABINA_ELEVADOR
    public int mConos = 2;            // m <= MAX_CONOS

    // Timers (en milisegundos)
    public int timerCaseta = 600;
    public int timerCirculacion = 800;
    public int timerEstacionar = 1000;
    public int timerEstanciaUniversidad = 2500;
    public int timerConoGuardia = 2000;
    public int timerCruce = 500;

    // Red TCP (Agente Distribuido)
    public String hostPisoRemoto = "127.0.0.1";
    public int puertoPisoRemoto = 5050;
}