package taskqueue.task;

/**
 * Created by 18729 on 10/15/2018.
 */
public class TaskFactory {

    /**
     * It support only default task now.
     * If need new kind of task, please add it in the 'else' block.
     * @return a new instance of task
     * */
    public static AbstractTask newTask(String kind, long id) {
        if ("default".equalsIgnoreCase(kind) || null == kind || "".equalsIgnoreCase(kind.trim())) {
            return new DefaultTaskImp(id);
        } else {
            return null;
        }
    }

    public static AbstractTask newTask(String kind) {
        if ("default".equalsIgnoreCase(kind) || null == kind || "".equalsIgnoreCase(kind.trim())) {
            long id =  (TaskFactory.class.hashCode() ^ (int) System.nanoTime());
            return new DefaultTaskImp(id);
        } else {
            return null;
        }
    }
}
