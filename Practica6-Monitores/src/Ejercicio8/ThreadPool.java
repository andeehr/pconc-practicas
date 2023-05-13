package Ejercicio8;

public class ThreadPool {

    private Buffer buffer;
    private Worker[] workers;

    public ThreadPool(int bufferSize, int threads) {
        this.buffer = new Buffer(bufferSize);
        CreateWorkers(threads);
    }

    private void CreateWorkers(int threads) {
        for (int j = 0; j < threads; j++) {
            workers[j] = new Worker(this.buffer);
        }
    }

    public void launch(DummyTask task) throws InterruptedException {
        this.buffer.producir(task);
    }
}
