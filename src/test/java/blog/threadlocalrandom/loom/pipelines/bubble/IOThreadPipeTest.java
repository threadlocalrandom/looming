package blog.threadlocalrandom.loom.pipelines.bubble;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IOThreadPipeTest {

    @Test
    void throughputTest() {
        IOThreadPipe throughput = new IOThreadPipe();
        ArrayList<Integer> arrayList = new ArrayList<>();
        try(ExecutorService es = Executors.newVirtualThreadExecutor()) {
            es.submit(() -> {
                IntStream.range(1, 4).forEach(throughput::push);
                throughput.push(Integer.MIN_VALUE);
            });
            es.submit(() -> {
                int read = throughput.next();
                while(read != Integer.MIN_VALUE) {
                    arrayList.add(read);
                    read = throughput.next();
                }
            });
        }
        assertEquals(3, arrayList.size());
        assertEquals(Arrays.asList(1,2,3), arrayList);

    }

}