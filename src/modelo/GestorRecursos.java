package modelo;

import java.util.concurrent.Semaphore;

public class GestorRecursos {
    public final BufferGenerico cvCasetaEntrada;    // Buffer 1
    public final BufferGenerico csCasetaSalida;     // Buffer 1
    public final BufferGenerico cpCasetaPeatonal;   // Buffer 1
    public final BufferGenerico mzZonaManiobra;     // Buffer 1

    public final BufferGenerico cuCubiculosAuto;    // Buffer m
    public final BufferGenerico cbCubiculosBici;    // Buffer m
    public final BufferGenerico qCallePiso;         // Buffer m
    public final BufferGenerico ewEsperaElevador;   // Buffer m
    public final BufferGenerico ecCabinaElevador;   // Buffer m
    public final BufferGenerico cnConosGuardia;     // Buffer m

    public final CrucePeatonalMonitor cruceX;       // Monitor X (Xp / Xv)
    public final Semaphore semaforoCupoAp;          // Semáforo contador Ap

    public GestorRecursos(ConfiguracionSimulador cfg) {
        cvCasetaEntrada = new BufferGenerico("Cv (Caseta Entrada)", 1);
        csCasetaSalida = new BufferGenerico("Cs (Caseta Salida)", 1);
        cpCasetaPeatonal = new BufferGenerico("Cp (Caseta Peatonal)", 1);
        mzZonaManiobra = new BufferGenerico("Mz (Zona Maniobra)", 1);

        cuCubiculosAuto = new BufferGenerico("Cu (Cubículos Auto)", cfg.mCubiculosAuto);
        cbCubiculosBici = new BufferGenerico("Cb (Cubículos Bici)", cfg.mCubiculosBici);
        qCallePiso = new BufferGenerico("Q (Calle Piso)", cfg.mCallePiso);
        ewEsperaElevador = new BufferGenerico("Ew (Espera Elevador)", cfg.mEsperaElevador);
        ecCabinaElevador = new BufferGenerico("Ec (Cabina Elevador)", cfg.mCabinaElevador);
        cnConosGuardia = new BufferGenerico("Cn (Conos Guardia)", cfg.mConos);

        cruceX = new CrucePeatonalMonitor(cfg.mCrucePeatonal);
        semaforoCupoAp = new Semaphore(cfg.mCubiculosAuto, true);
    }
}