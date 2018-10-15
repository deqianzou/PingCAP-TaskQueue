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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if(o == null)
            return false;
        if (o.getClass() == this.getClass()) {
            AbstractTask that = (AbstractTask) o;
            return this.getId() == that.getId();
        }
        else
            return false;
    }

    public enum States {
        RUNNING,
        FINISHED,
        RUNNABLE,
    }
}
