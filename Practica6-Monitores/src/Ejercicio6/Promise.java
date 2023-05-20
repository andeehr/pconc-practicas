package Ejercicio6;

public class Promise implements Future {

    private int operator = 2;
    private int result = 0;

    @Override
    public synchronized int get() throws InterruptedException {
        while (result == 0) {
            wait();
        }
        return result;
    }

    public synchronized void set(int result) throws InterruptedException {
        Thread.sleep(2000);
        result = result * operator;
        notify();
    }
}
