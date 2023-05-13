package Ejercicio8;

public class Worker extends Thread {
    private Buffer buffer;

    public Worker (Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                buffer.consumir().run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
