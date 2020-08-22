package blog.threadlocalrandom.loom.pipelines.bubble;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class IOThreadPipe implements ThreadPipe {

    PipedOutputStream pipeOut = new PipedOutputStream();
    PipedInputStream pipeIn = new PipedInputStream();

    public IOThreadPipe() {
        try {
            pipeOut.connect(pipeIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void push(Integer value) {

        try {
            pipeOut.write(ByteBuffer.allocate(4).putInt(value).array());
            pipeOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Integer next() {
        byte[] inBytes = new byte[4];
        IntBuffer buffer = ByteBuffer.wrap(inBytes).asIntBuffer();
        try {
            int bytesRead = pipeIn.read(inBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return Integer.MIN_VALUE;
        }
        return buffer.get();
    }
}
