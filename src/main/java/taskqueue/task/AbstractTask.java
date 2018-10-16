package taskqueue.task;

/**
 * Created by deqianzou on 10/10/2018.
 */
public abstract class AbstractTask implements Runnable {

    private TaskId id;

    public AbstractTask(TaskId id) {
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

    public TaskId getId() {
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
            return this.getId().equals(that.getId());
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
