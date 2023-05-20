// simulacro 
// 1a
public class Encoder {
    private Frame[] buffer;

    public Buffer(int M) {
        this.buffer = new ArrayList();
    }

    public synchronized void putRawFrame(frame) throws InterruptedException {
        while(isFull()) {
            wait();
        }
        frames.add(frame);
        notify();
    }

    public synchronized Frame[] getPack() throws InterruptedException {
        while(!canGetPack()) {
            wait();
        }
        Frame[] result = frames.remove(P) // asumimos que saca todos
        notifyAll();
        return result;
    }

    private synchronized boolean isFull() {
        return frames.size() == M;
    }

    private synchronized boolean isEmpty() {
        return frames.size() >= P;
    }
}

//1b
public class Encoder {
    private Frame[] buffer;

    public Buffer(int M) {
        this.buffer = new ArrayList();
    }

    public synchronized void putRawFrame(frame) throws InterruptedException {
        while(isFull()) {
            wait();
        }
        frames.add(frame);
        notifyAll();
    }

    public synchronized Frame[] getPack(int p) throws InterruptedException {
        while(!canGetPack(p)) {
            wait();
        }
        Frame[] result = frames.remove(p) // asumimos que saca todos
        notifyAll();
        return result;
    }

    private synchronized boolean isFull() {
        return frames.size() == M;
    }

    private synchronized boolean isEmpty(int p) {
        return frames.size() >= p;
    }
}
