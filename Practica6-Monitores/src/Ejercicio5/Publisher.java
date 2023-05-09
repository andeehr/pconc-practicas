package Ejercicio5;

public class Publisher extends Thread {
    private Event event;

    public Publisher(Event event) {
        this.event = event;
    }

    @Override
    public void run() {
        event.publish("Holis");
    }
}
