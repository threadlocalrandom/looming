package blog.threadlocalrandom.loom;

import blog.threadlocalrandom.loom.deadlock.DeadlockDetector;
import blog.threadlocalrandom.loom.pipelines.bubble.LoomedBubbleSort;

import java.math.BigInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class MainLooming {

    private static final Logger LOG = Logger.getLogger(MainLooming.class.getName());

    public static void main(String[] args) {
//        deadlockTest();
//        factorial();
        loomedBubbleSort();
    }

    private static void loomedBubbleSort() {
        LoomedBubbleSort bubbleSort = new LoomedBubbleSort(256);
        System.out.println("Setting up");
        bubbleSort.setup();
        System.out.println("Running");
        long then = System.currentTimeMillis();
        bubbleSort.sort();
        System.out.printf("Time = %dms", (System.currentTimeMillis() - then));
        // bubbleSort.printResults();

    }

    private static void factorial() {
        for(int t=0;t<10;t++) {
            long then = System.nanoTime();

            BigInteger result = IntStream.rangeClosed(1, 200_000).parallel().mapToObj(BigInteger::valueOf).reduce(BigInteger.ONE, BigInteger::multiply);
            LOG.info("" + (System.nanoTime() - then) / 1_000_000);
        }
    }

    private static void deadlockTest() throws InterruptedException {
        ScheduledExecutorService deadLockExecutor = Executors.newScheduledThreadPool(1);
        deadLockExecutor.scheduleAtFixedRate(new DeadlockDetector(), 5, 10, TimeUnit.SECONDS);
        String lock1 = "Lock1";
        String lock2 = "Lock2";
        Thread thread1 = Thread.builder().virtual().name("Thread1").task(() -> {
            synchronized (lock1) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2) {

                }
            }
        }).build();
        Thread thread2 = Thread.builder().name("Thread2").task(() -> {
            synchronized (lock2) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock1) {

                }
            }
        }).build();
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }
}
