package taskqueue.queue;

import org.junit.Test;
import org.junit.Assert;
import taskqueue.task.AbstractTask;
import taskqueue.task.TaskFactory;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by deqianzou on 10/14/2018.
 */
public class TestLinkedTaskQueue {

    private Queue linkedTaskQueue = LinkedTaskQueue.getInstance();

    final ExecutorService pool = Executors.newCachedThreadPool();

    final AtomicLong addSum = new AtomicLong(0) ;

    final AtomicLong getSum = new AtomicLong(0) ;

    final int nTrials = 10, nPairs = 10;

    private CyclicBarrier barrier = new CyclicBarrier(2 * nPairs + 1);

    @Test
    public void testShutDownAndReset() {
        linkedTaskQueue.reset();
        Assert.assertTrue(linkedTaskQueue.is_closed() == Boolean.FALSE);
        Assert.assertTrue(linkedTaskQueue.size() == 0);
        linkedTaskQueue.shutdown();
        Assert.assertTrue(linkedTaskQueue.is_closed() == Boolean.TRUE);
        linkedTaskQueue.reset();
        Assert.assertTrue(linkedTaskQueue.is_closed() == Boolean.FALSE);
    }

    @Test
    public void testAdd() {
        linkedTaskQueue.reset();
        AbstractTask task = TaskFactory.newTask("null");
        boolean res = linkedTaskQueue.add(task);
        Assert.assertTrue(res == Boolean.FALSE);
        task = TaskFactory.newTask("default", 10);
        task.setState(AbstractTask.States.FINISHED);
        try {
            res = linkedTaskQueue.add(task);
        } catch (AssertionError e) {
            e.printStackTrace();
        }
        Assert.assertTrue(res == Boolean.FALSE);
        task.setState(AbstractTask.States.RUNNING);
        res = linkedTaskQueue.add(task);
        Assert.assertTrue(res == Boolean.FALSE);
        task.setState(AbstractTask.States.RUNNABLE);
        res = linkedTaskQueue.add(task);
        Assert.assertTrue(res == Boolean.TRUE);
        Assert.assertTrue(linkedTaskQueue.size() == 1);
        linkedTaskQueue.shutdown();
        res = linkedTaskQueue.add(task);
        Assert.assertTrue(res == Boolean.FALSE);
    }

    @Test
    public void testGet() {
        linkedTaskQueue.reset();
        Assert.assertTrue(linkedTaskQueue.get() == null);
        AbstractTask task = TaskFactory.newTask("default", 10);
        linkedTaskQueue.add(task);
        AbstractTask res = linkedTaskQueue.get();
        Assert.assertTrue(res.equals(task));
        Assert.assertTrue(res.getId() == 10);
        Assert.assertTrue(res.getState() == AbstractTask.States.RUNNING);
        linkedTaskQueue.shutdown();
        res = linkedTaskQueue.get();
        Assert.assertTrue(res == null);
    }

    @Test
    public void testDone() {
        linkedTaskQueue.reset();
        AbstractTask task = TaskFactory.newTask("default", 10);
        linkedTaskQueue.add(task);
        task.setState(AbstractTask.States.FINISHED);
        boolean res = linkedTaskQueue.done(null);
        Assert.assertTrue(res == Boolean.FALSE);
        res = linkedTaskQueue.done(task);
        Assert.assertTrue(res == Boolean.TRUE);
        task.setState(AbstractTask.States.RUNNABLE);
        linkedTaskQueue.add(task);
        task.setState(AbstractTask.States.FINISHED);
        linkedTaskQueue.shutdown();
        res = linkedTaskQueue.done(task);
        Assert.assertTrue(res == Boolean.FALSE);
    }

    @Test
    public void testThreadSecurity() {
        try {
            for (int i=0; i<nPairs; i++) {
                pool.execute(new Producer());
                pool.execute(new Consumer());
            }
            barrier.await();
            // barrier.await();
            Assert.assertTrue(addSum.get() == getSum.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int xorShift(int x) {
        if ((x&1) == 0)
            x = x >> 7;
        else
            x = x<<3;

        return x;
    }

    class Producer implements Runnable {

        public void run() {

            try {
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                AbstractTask task = task = TaskFactory.newTask("default", seed);
                int sum = 0;
                barrier.await();

                for (int i = nTrials; i > 0; --i) {
                    linkedTaskQueue.add(task);
                    sum += seed;
                    seed = xorShift(seed);
                }

                addSum.getAndAdd(sum) ;

                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Consumer implements Runnable {

        public void run() {
            try {
                barrier.await() ;
                long sum = 0;

                for (int i = nTrials; i > 0; --i) {
                    AbstractTask task = linkedTaskQueue.get();
                    if (null != task)  sum += task.getId() ;
                }

                getSum.getAndAdd(sum) ;

                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
