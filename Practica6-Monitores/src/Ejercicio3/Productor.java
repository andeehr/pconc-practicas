package Ejercicio3;

public class Productor extends Thread {

    private Buffer buffer;

    public Productor (Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            try {
                buffer.producir(i);
                i++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
