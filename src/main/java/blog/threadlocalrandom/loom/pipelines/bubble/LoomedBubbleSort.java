package blog.threadlocalrandom.loom.pipelines.bubble;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Arrays.setAll;

public class LoomedBubbleSort {
    int[] unsorted;
    List<Worker> workers;
    Terminator terminator;
    ThreadPipe primer;
    public LoomedBubbleSort(int size) {
        unsorted = new int[size];
    }
    public void setup() {
        Random generator = new Random();
        setAll(unsorted, (index) -> Math.abs(generator.nextInt()));
        workers = new ArrayList<>(unsorted.length);
        primer = new QueuedThreadPipe();
        Worker firstWorker = new Worker(primer, new QueuedThreadPipe());
        workers.add(firstWorker);
        for(int i = 1; i < unsorted.length-1; i++) {
            workers.add(new Worker(workers.get(i-1).getDownstreamPipe(), new QueuedThreadPipe()));
        }
        terminator = new Terminator(workers.get(workers.size()-1).getDownstreamPipe());
    }
    public void sort() {
        try(ExecutorService virtualExecutor = Executors.newVirtualThreadExecutor()) {
            virtualExecutor.submit(() -> {
                for(int i : unsorted) {
                    primer.push(i);
                }
                primer.push(Integer.MIN_VALUE);

            });
             virtualExecutor.submitTasks(workers);
             virtualExecutor.submitTask(terminator);

        }
        //System.out.println(terminator.getResult());

    }

    public void printResults() {
        System.out.println(terminator.getResult());
    }

}
