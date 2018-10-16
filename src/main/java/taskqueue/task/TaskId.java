package taskqueue.task;

/**
 * Created by 18729 on 10/16/2018.
 */
public class TaskId {
    private String id;

    public TaskId(String id) {
        this.id = id;
    }

    public TaskId(int id) {
        this.id = String.valueOf(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if(o == null)
            return false;
        if (o.getClass() == this.getClass()) {
            TaskId that = (TaskId) o;
            return id.equals(that.getId());
        }
        else
            return false;
    }

    public String getId() {
        return id;
    }
}
