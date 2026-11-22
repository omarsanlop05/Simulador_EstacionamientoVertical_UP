package modelo;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CrucePeatonalMonitor {
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition puedePasarPeaton = lock.newCondition();
    private final Condition puedePasarAuto = lock.newCondition();

    private final int pMax;
    private int peatonesCruzando = 0;
    private boolean autoCruzando = false;
    private int peatonesEsperando = 0;
    private int autosEsperando = 0;

    public CrucePeatonalMonitor(int pMax) {
        this.pMax = pMax;
    }

    // Xp: entrar peatón
    public void entrarPeaton() throws InterruptedException {
        lock.lock();
        try {
            peatonesEsperando++;
            while (autoCruzando || peatonesCruzando >= pMax) {
                puedePasarPeaton.await();
            }
            peatonesEsperando--;
            peatonesCruzando++;
        } finally {
            lock.unlock();
        }
    }

    // Xp': salir peatón
    public void salirPeaton() {
        lock.lock();
        try {
            peatonesCruzando--;
            if (peatonesCruzando == 0) {
                puedePasarAuto.signal();
            }
            puedePasarPeaton.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // Xv: entrar auto (solo si peatonesCruzando == 0 y !autoCruzando)
    public void entrarAuto() throws InterruptedException {
        lock.lock();
        try {
            autosEsperando++;
            while (autoCruzando || peatonesCruzando > 0) {
                puedePasarAuto.await();
            }
            autosEsperando--;
            autoCruzando = true;
        } finally {
            lock.unlock();
        }
    }

    // Xv': salir auto
    public void salirAuto() {
        lock.lock();
        try {
            autoCruzando = false;
            puedePasarPeaton.signalAll();
            puedePasarAuto.signal();
        } finally {
            lock.unlock();
        }
    }

    public int getPeatonesCruzando() { return peatonesCruzando; }
    public int getAutosCruzando() { return autoCruzando ? 1 : 0; }
    public int getEsperandoTotal() { return peatonesEsperando + autosEsperando; }
}