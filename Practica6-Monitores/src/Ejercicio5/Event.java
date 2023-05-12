package Ejercicio5;

public class Event {

    private int published = 0;
    private int subscribed = 0;

    public synchronized void publish(String occurrence) {
        published++;
        System.out.println(occurrence);
        notifyAll();
    }

    public synchronized void subscribe() throws InterruptedException {
        subscribed++;
        while (subscribed > published) {
            wait();
        }
    }
}
