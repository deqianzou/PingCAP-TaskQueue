package taskqueue.task;

/**
 * Created by deqianzou on 10/10/2018.
 */
public abstract class AbstractTask implements Runnable {

    private long id;

    public AbstractTask(long id) {
        this.id = id;
    }

    private volatile States state;

    public void run() {
        this.state = States.RUNNING;
    }

    public States getState() {
        return state;
    }

    public void setState(States state) {
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public abstract boolean equals(AbstractTask task);

    public enum States {
        RUNNING,
        FINISHED,
        RUNNABLE,
    }
}
