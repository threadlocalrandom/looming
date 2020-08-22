package blog.threadlocalrandom.loom.pipelines.bubble;

import java.util.concurrent.Callable;

public class Worker implements Callable<Integer> {

    ThreadPipe upstreamPipe;
    ThreadPipe downstreamPipe;
    Integer currentValue = null;

    /**
     * Values are taken from the downstream Pipe, compared to the currentValue and the higher value is pushed to the upstream Pipe
     * @param upstreamPipe the Pipe to be read from
     * @param downstreamPipe the Pipe to be written to
     */
    public Worker(ThreadPipe upstreamPipe, ThreadPipe downstreamPipe) {
        this.upstreamPipe = upstreamPipe;
        this.downstreamPipe = downstreamPipe;
    }

    @Override
    public Integer call() {

            for(int takenInt = upstreamPipe.next(); takenInt != Integer.MIN_VALUE; takenInt = upstreamPipe.next()) {

                if(currentValue == null) {
                    currentValue = takenInt;
                } else if(takenInt < currentValue) {
                    downstreamPipe.push(currentValue);
                    currentValue = takenInt;
                } else {
                    downstreamPipe.push(takenInt);
                }
               // Thread.yield();
            }
            downstreamPipe.push(currentValue);
            downstreamPipe.push(Integer.MIN_VALUE);
            return currentValue;
    }

    public ThreadPipe getUpstreamPipe() {
        return this.upstreamPipe;
    }
    public ThreadPipe getDownstreamPipe() {
        return this.downstreamPipe;
    }
}
