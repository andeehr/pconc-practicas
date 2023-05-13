package Ejercicio8;

public class Buffer {
    private DummyTask[] buffer;
    private int size;
    private int writePointer;
    private int readPointer;

    public Buffer(int size) {
        this.size = size;
        this.buffer = new DummyTask[size + 1];
        this.writePointer = 0;
        this.readPointer = 0;
    }

    public synchronized void producir(DummyTask task) throws InterruptedException {
        while(isFull()) {
            wait();
        }
        this.buffer[writePointer] = task;
        writePointer = (writePointer + 1) % size;
        notifyAll();
    }

    public synchronized DummyTask consumir() throws InterruptedException {
        while(isEmpty()) {
            wait();
        }
        DummyTask result = this.buffer[readPointer];
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
