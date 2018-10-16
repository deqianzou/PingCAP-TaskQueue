package taskqueue.task;

/**
 * Created by deqianzou on 10/10/2018.
 */
public class DefaultTaskImp extends AbstractTask {

    public DefaultTaskImp(TaskId id) {
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
        return this.getId().equals(task.getId());
    }

}
