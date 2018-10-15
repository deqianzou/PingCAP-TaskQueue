package taskqueue.task;

/**
 * Created by deqianzou on 10/10/2018.
 */
public class DefaultTaskImp extends AbstractTask {

    public DefaultTaskImp(long id) {
        super(id);
        setState(States.RUNNABLE);
    }

    @Override
    public void run() {
        System.out.print("the task has not been realized..." + "id: " + getId());
        setState(States.FINISHED);
    }

    public boolean equals(AbstractTask task) {
        if (task == null)  return Boolean.FALSE;
        return this.getId() == task.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if(o == null)
            return false;
        if (o.getClass() == this.getClass()) {
            DefaultTaskImp that = (DefaultTaskImp) o;
            return this.getId() == that.getId();
        }
        else
            return false;
    }

}
