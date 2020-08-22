package blog.threadlocalrandom.loom.pipelines.bubble;

public interface ThreadPipe {

    void push(Integer value);

    Integer next();
}
