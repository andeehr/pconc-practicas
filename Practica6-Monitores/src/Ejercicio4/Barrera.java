package Ejercicio4;

public class Barrera {
    private int permisos;
    private int inicial;

    public Barrera(int permisos) {
        this.permisos = permisos;
        this.inicial = 0;
    }

    public synchronized void esperar() throws InterruptedException {
        inicial++;
        while (inicial < permisos) {
            wait();
        }
        notify();
    }
}
