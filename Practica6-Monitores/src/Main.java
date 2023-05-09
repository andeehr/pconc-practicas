import Ejercicio1.Contador;
import Ejercicio3.Buffer;
import Ejercicio3.Consumidor;
import Ejercicio3.Productor;
import Ejercicio4.Barrera;
import Ejercicio4.BarreraThread;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // prueba ej 3
//        Buffer buffer = new Buffer(2);
//        Productor productor = new Productor(buffer);
//        Consumidor consumidor = new Consumidor(buffer);
//
//        productor.start();
//        consumidor.start();
//
//        Thread.sleep(2000);
//        productor.interrupt();
//        consumidor.interrupt();

        Barrera barrera = new Barrera(3);
        BarreraThread t1 = new BarreraThread(barrera, "a", "1");
        BarreraThread t2 = new BarreraThread(barrera, "b", "2");
        BarreraThread t3 = new BarreraThread(barrera, "c", "3");

        t1.start();
        t2.start();
        t3.start();
    }
}