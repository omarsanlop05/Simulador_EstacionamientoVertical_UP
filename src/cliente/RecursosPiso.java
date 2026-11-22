package cliente;

import modelo.BufferGenerico;
import modelo.ConfiguracionSimulador;
import modelo.CrucePeatonalMonitor;

// Buffers locales de un piso: cada computadora conectada instancia los suyos.
public class RecursosPiso {
    public final BufferGenerico mzZonaManiobra;     // Buffer 1
    public final BufferGenerico cuCubiculosAuto;    // Buffer m
    public final BufferGenerico cbCubiculosBici;    // Buffer m
    public final BufferGenerico qCallePiso;         // Buffer m
    public final BufferGenerico ewEsperaElevador;   // Buffer m
    public final BufferGenerico cnConosGuardia;     // Buffer m
    public final CrucePeatonalMonitor cruceX;       // Monitor X (Xp / Xv)

    public RecursosPiso(ConfiguracionSimulador cfg) {
        mzZonaManiobra = new BufferGenerico("Mz (Zona Maniobra)", 1);
        cuCubiculosAuto = new BufferGenerico("Cu (Cubículos Auto)", cfg.mCubiculosAuto);
        cbCubiculosBici = new BufferGenerico("Cb (Cubículos Bici)", cfg.mCubiculosBici);
        qCallePiso = new BufferGenerico("Q (Calle Piso)", cfg.mCallePiso);
        ewEsperaElevador = new BufferGenerico("Ew (Espera Elevador)", cfg.mEsperaElevador);
        cnConosGuardia = new BufferGenerico("Cn (Conos Guardia)", cfg.mConos);
        cruceX = new CrucePeatonalMonitor(cfg.mCrucePeatonal);
    }
}
