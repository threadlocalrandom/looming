package blog.threadlocalrandom.loom.pipelines.bubble;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class Terminator implements Callable<List<Integer>> {

    ThreadPipe downPipe;
    List<Integer> result = new LinkedList<>();
    public Terminator(ThreadPipe downPipe) {
        this.downPipe = downPipe;
    }


    @Override
    public List<Integer> call() {
            for(Integer i = downPipe.next(); i != Integer.MIN_VALUE; i = downPipe.next()) {
                result.add(i);
            }
        return result;
    }

    public List<Integer> getResult() { return result; }
}
