package taskqueue.queue;

import com.sun.istack.internal.NotNull;
import taskqueue.task.AbstractTask;

import javax.swing.text.StyledEditorKit;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by 18729 on 10/10/2018.
 */
public class SyncTaskQueue implements Queue {
    private LinkedList<AbstractTask> taskQueue;
    private boolean shutDown;

    public SyncTaskQueue() {
        taskQueue = new LinkedList<AbstractTask>();
        shutDown = Boolean.FALSE;
    }

    public synchronized boolean isEmpty() {
        return taskQueue.isEmpty();
    }

    public synchronized boolean add(@NotNull AbstractTask task) {
        assert (task.getState() == AbstractTask.States.RUNNABLE);
        if (shutDown)  return Boolean.FALSE;
        return taskQueue.add(task);
    }

    public synchronized AbstractTask get() {
        if (shutDown) return null;
        AbstractTask task;
        Iterator iterator = taskQueue.iterator();
        while (iterator.hasNext()){
            task = (AbstractTask) iterator.next();
            if (AbstractTask.States.RUNNABLE == task.getState()) {
                task.setState(AbstractTask.States.RUNNING);
                return task;
            }
        }
        return null;
    }

    public synchronized boolean done(@NotNull AbstractTask abstractTask) {
        abstractTask.setState(AbstractTask.States.FINISHED);
        if (shutDown)  return Boolean.FALSE;
        taskQueue.remove(abstractTask);
        AbstractTask task = taskQueue.remove(0);
        return null != task;
    }

    public synchronized void shutdown() {
        shutDown = Boolean.TRUE;
    }

    public synchronized boolean is_closed() {
        return shutDown;
    }

    public synchronized int size(){
        return taskQueue.size();
    }

    public synchronized void reset() {
        shutDown = Boolean.TRUE;
    }
}
