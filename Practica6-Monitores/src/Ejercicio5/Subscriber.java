package Ejercicio5;

public class Subscriber extends Thread {
    private Event event;

    public Subscriber(Event event) {
        this.event = event;
    }

    @Override
    public void run() {
        try {
            System.out.println("Me suscribí");
            event.subscribe();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
