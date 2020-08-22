package blog.threadlocalrandom.loom.pipelines.bubble;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.Arrays.setAll;

class WorkerTest {

    @Test
    void call() {
        Integer[] unsorted = new Integer[100_000];
        Random generator = new Random();
        setAll(unsorted, (index) -> Math.abs(generator.nextInt(100)));


        ThreadPipe primer = new QueuedThreadPipe();
        ThreadPipe terminal = new QueuedThreadPipe();
        LinkedList<Worker> workers = new LinkedList<>();
        Worker worker = new Worker(primer, new QueuedThreadPipe());
        workers.add(worker);
        for(int i=1; i< unsorted.length; i++) {
            Worker newWorker = new Worker(workers.getLast().getDownstreamPipe(), new QueuedThreadPipe());
            workers.add(newWorker);
        }
        workers.add(new Worker(workers.getLast().getDownstreamPipe(), terminal));
        var result = new ArrayList<Integer>();
        try (ExecutorService es = Executors.newVirtualThreadExecutor()) {
            es.submit(() -> {
                AtomicInteger pushed= new AtomicInteger(1);
                Stream.of(unsorted).forEach(primer::push);
                primer.push(Integer.MIN_VALUE);
            });
            es.submitTasks(workers);
            es.submit(() -> {
                int read = terminal.next();
                while(read != Integer.MIN_VALUE) {
                    result.add(read);
                    read = terminal.next();
                }
            });
        }

    }
}