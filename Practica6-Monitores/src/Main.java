import Ejercicio1.Contador;
import Ejercicio3.Buffer;
import Ejercicio3.Consumidor;
import Ejercicio3.Productor;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Buffer buffer = new Buffer(2);
        Productor productor = new Productor(buffer);
        Consumidor consumidor = new Consumidor(buffer);

        productor.start();
        consumidor.start();

        Thread.sleep(2000);
        productor.interrupt();
        consumidor.interrupt();
    }
}