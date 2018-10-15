package taskqueue.queue;

import com.sun.istack.internal.NotNull;
import taskqueue.task.AbstractTask;

/**
 * Created by deqianzou on 10/10/2018.
 */
public interface Queue {

    boolean isEmpty();

    /**
     * @return A Bool flag, true represents that the execution was success and false not.
     * */
    boolean add(@NotNull AbstractTask task);

    /**
     * @return A runnable Abstract object, null represents that the execution failed.
     * */
    AbstractTask get();

    /**
     * @return A Bool flag, true represents that the execution was success and false not.
     * */
    boolean done(@NotNull AbstractTask task);

    void shutdown();

    boolean is_closed();

    int size();

    void reset();
}
