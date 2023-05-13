package Ejercicio5;

public class Event {

    private int published = 0;

    public synchronized void publish(String occurrence) {
        published++;
        System.out.println(occurrence);
        notifyAll();
    }

    public synchronized void subscribe() throws InterruptedException {
        int miPublicacion = published + 1;
        while (miPublicacion > published) {
            wait();
        }
    }
}
