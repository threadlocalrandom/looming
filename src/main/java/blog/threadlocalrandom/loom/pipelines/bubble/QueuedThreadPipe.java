package blog.threadlocalrandom.loom.pipelines.bubble;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public class QueuedThreadPipe implements ThreadPipe {


    final BlockingQueue<Integer> loadingQueue;
    public QueuedThreadPipe() {
        loadingQueue = new LinkedTransferQueue<>();
    }

    @Override
    public void push(Integer value) {
        loadingQueue.add(value);
    }

    @Override
    public Integer next()  {

        try {
            return loadingQueue.take();
        } catch (InterruptedException e) {
            return Integer.MIN_VALUE;
        }
    }

}
