package Ejercicio1;

public class Contador {
    private int actual = 0;

    public synchronized void incrementar() {
        actual++;
    }

    public synchronized void decrementar() {
        actual--;
    }

    public synchronized int getActual() {
        return actual;
    }
}
