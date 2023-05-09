package Ejercicio3;

public class Buffer {
    private int[] buffer;
    private int size;
    private int writePointer;
    private int readPointer;

    public Buffer(int tamanio) {
        this.size = tamanio;
        this.buffer = new int[tamanio + 1];
        this.writePointer = 0;
        this.readPointer = 0;
    }

    public synchronized void producir(int number) throws InterruptedException {
        while(isFull()) {
            wait();
        }
        this.buffer[writePointer] = number;
        writePointer = (writePointer + 1) % size;
        notifyAll();
    }

    public synchronized int consumir() throws InterruptedException {
        while(isEmpty()) {
            wait();
        }
        int result = this.buffer[readPointer];
        readPointer = (readPointer + 1) % size;
        notifyAll();
        return result;
    }

    private synchronized boolean isFull() {
        return writePointer == readPointer;
    }

    private synchronized boolean isEmpty() {
        return (writePointer + 1) % size == readPointer;
    }
}
