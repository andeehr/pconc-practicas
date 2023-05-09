package Ejercicio4;

public class BarreraThread extends Thread {

    private Barrera barrera;
    private String s;
    private String n;

    public BarreraThread (Barrera barrera, String s, String n) {
        this.barrera = barrera;
        this.s = s;
        this.n = n;
    }

    @Override
    public void run() {
        try {
            System.out.println(s);
            barrera.esperar();
            System.out.println(n);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
