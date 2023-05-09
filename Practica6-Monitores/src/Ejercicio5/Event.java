package Ejercicio5;

public class Event {

    private boolean hayPublicacion;
    private String occurrence;

    public Event() {
        this.hayPublicacion = false;
    }

    public synchronized void publish(String occurrence) {
        this.occurrence = occurrence;
        this.hayPublicacion = true;
        notify();
    }

    public synchronized void subscribe() throws InterruptedException {
        while (!hayPublicacion) {
            wait();
        }
        System.out.println(occurrence);
    }
}
