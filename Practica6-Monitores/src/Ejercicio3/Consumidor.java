package Ejercicio3;

public class Consumidor extends Thread {

    private Buffer buffer;

    public Consumidor (Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int result = buffer.consumir();
                System.out.println(result);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
