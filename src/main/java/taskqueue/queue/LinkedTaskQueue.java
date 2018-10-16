package taskqueue.queue;

import com.sun.istack.internal.NotNull;
import taskqueue.task.AbstractTask;
import taskqueue.task.TaskId;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by deqianzou on 10/14/2018.
 * TODO: split runnable task queue and running task queue
 *
 * This Queue is based on linkedList and does not have any size demand.
 * All Tasks is stored in this queue, and thus the time complexity of the method `get()`
 * is not as good as a ideal queue.
 */
public class LinkedTaskQueue implements Queue {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private AtomicBoolean shutDown;
    private AtomicInteger size;
    private Node head;
    private Node tail;

    private HashMap<TaskId, AbstractTask> runningTaskMap;

    public static Queue getInstance() {
        return queueHolder.linkedTaskQueue;
    }

    private LinkedTaskQueue() {
        this.shutDown = new AtomicBoolean(Boolean.FALSE);
        this.size = new AtomicInteger(0);
        head = new Node(null);
        tail = new Node(null);
        head.next = tail;
        tail.pre = head;
        runningTaskMap = new HashMap<TaskId, AbstractTask>();
    }

    public boolean isEmpty() {
        return 0==size.get();
    }

    // TODO: throw new exception when shutDown `QueueShutDownException`
    public boolean add(@NotNull AbstractTask task) {
        boolean res = Boolean.FALSE;
        try {
            res = _add(task);
        } catch (AssertionError e) {
            res = Boolean.FALSE;
        }
        return res;
    }

    // TODO: throw new exception when shutDown `QueueShutDownException`
    public AbstractTask get() {
        if (shutDown.get())  return null;
        Node e = head.next;
        lock.readLock().lock();
        try {
            while (e != tail) {
                if (AbstractTask.States.RUNNABLE == e.task.getState()){
                    lock.readLock().unlock();
                    lock.writeLock().lock();
                    try {
                        if (e.task.getState() == AbstractTask.States.RUNNABLE){
                            e.task.setState(AbstractTask.States.RUNNING);
                            // runningTaskMap.put(e.task.getId(), e.task);
                            e.pre.next = e.next;
                            e.next.pre = e.pre;
                            e.pre = null;
                            e.next = null;
                            return e.task;
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    finally {
                        lock.writeLock().unlock();
                        lock.readLock().lock();
                    }
                }
                e = e.next;
            }
        }
        finally {
            lock.readLock().unlock();
        }
        return null;
    }

    // TODO: throw new exception when shutDown `QueueShutDownException`
    public boolean done(@NotNull AbstractTask task) {
        boolean res = Boolean.FALSE;
        try {
            res = _done(task);
        } catch (AssertionError e) {
            res = Boolean.FALSE;
        }
        return res;
    }

    public void reset() {
        try {
            lock.writeLock().lock();
            shutDown.set(Boolean.TRUE);
            head.next = tail;
            tail.pre = head;
            size.set(0);
            shutDown.set(Boolean.FALSE);
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public void shutdown() {
        shutDown.set(Boolean.TRUE);
    }

    public boolean is_closed() {
        return shutDown.get();
    }

    public int size() {
        return size.get();
    }

    private boolean _add(@NotNull AbstractTask task) {
        if (shutDown.get())  return Boolean.FALSE;
        if (null == task) return Boolean.FALSE;
        assert AbstractTask.States.RUNNABLE == task.getState();
        lock.writeLock().lock();
        try {
            Node node = new Node(task);
            node.pre = tail.pre;
            tail.pre.next = node;
            node.next = tail;
            tail.pre = node;
            size.addAndGet(1);
            return Boolean.TRUE;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean _done(@NotNull AbstractTask task) {
        if (shutDown.get())  return Boolean.FALSE;
        if (null == task)  return Boolean.FALSE;
        assert AbstractTask.States.FINISHED == task.getState();
        lock.readLock().lock();
        try {
            Node e = head.next;
            while (e != tail && e.task.equals(task))
                e = e.next;
            if (e != tail) {
                lock.readLock().unlock();
                remove(e);
            }
            return Boolean.TRUE;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    private void remove(@NotNull Node e) {
        lock.writeLock().lock();
        try {
            e.pre.next = e.next;
            e.next.pre = e.pre;
            e.next = null;
            e.pre = null;
            e.task = null;
            size.getAndDecrement();
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    static class Node {
        AbstractTask task;
        Node next;
        Node pre;

        Node (AbstractTask task) {
            this.task = task;
            this.next = null;
            this.pre = null;
        }
    }

    private static class queueHolder {
        private static LinkedTaskQueue linkedTaskQueue = new LinkedTaskQueue();
    }
}