package blog.threadlocalrandom.loom.deadlock;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.Optional;

public class DeadlockDetector implements Runnable {

    public void scanForDeadlocks() {
        ThreadMXBean tmBean = ManagementFactory.getThreadMXBean();
        final Optional<long[]> monitorDeadlockedThreads = Optional.ofNullable(tmBean.findMonitorDeadlockedThreads());
        monitorDeadlockedThreads.ifPresentOrElse(tIds -> {
                    ThreadInfo[] threadInfos = tmBean.getThreadInfo(tIds);
                    List.of(threadInfos).forEach(ti -> {
                        System.out.println(ti.getThreadId());
                        System.out.println(ti.getThreadName());
                        System.out.println(ti.getLockName());
                        System.out.println(ti.getLockOwnerId());
                        System.out.println(ti.getLockOwnerName());
                    });
                }, () -> System.out.println("No deadlocks...")
        );


    }

    @Override
    public void run() {
        scanForDeadlocks();
    }
}
