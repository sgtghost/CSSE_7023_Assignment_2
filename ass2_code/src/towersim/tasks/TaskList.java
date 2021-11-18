package towersim.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Represents a circular list of tasks for an aircraft to cycle through.
 * @ass1
 */
public class TaskList {
    /** List of tasks to cycle through. */
    private final List<Task> tasks;
    /** Index of current task in tasks list. */
    private int currentTaskIndex;

    /**
     * Creates a new TaskList with the given list of tasks.
     * <p>
     * Initially, the current task (as returned by {@link #getCurrentTask()}) should be the first
     * task in the given list.
     *
     * The list of tasks should be validated to ensure that it complies
     * with the rules for task ordering.
     * If the given list is invalid, an {@code IllegalArgumentException} should be thrown.
     *
     * <ul>
     * <li>An empty task list is invalid.</li>
     * <li>Each task may only come immediately after a set of allowed tasks.
     * See the diagram below. For example, a {@code LAND} task may only come after
     * an {@code AWAY} task,
     * while a {@code WAIT} task may come after either a {@code LAND} task or
     * another {@code WAIT} task.</li>
     *
     * </ul>
     *
     * @param tasks list of tasks
     * @throws IllegalArgumentException if the given list of tasks is invalid
     * @ass2
     */
    public TaskList(List<Task> tasks) {
        String errorMessage = "The given list of tasks is invalid";
        if (tasks.size() == 0) {
            throw new IllegalArgumentException(errorMessage);
        } else {
            for (Task task : tasks) {
                int nextTaskIndex = (tasks.indexOf(task) + 1) % tasks.size();
                Task nextTask = tasks.get(nextTaskIndex);
                taskCheck(task, nextTask); //An individual method added below to check the task.
            }
        }
        this.tasks = tasks;
        this.currentTaskIndex = 0;
    }

    /**
     * Returns the current task in the list.
     *
     * @return current task
     * @ass1
     */
    public Task getCurrentTask() {
        return this.tasks.get(this.currentTaskIndex);
    }

    /**
     * Returns the task in the list that comes after the current task.
     * <p>
     * After calling this method, the current task should still be the same as it was before calling
     * the method.
     * <p>
     * Note that the list is treated as circular, so if the current task is the last in the list,
     * this method should return the first element of the list.
     *
     * @return next task
     * @ass1
     */
    public Task getNextTask() {
        int nextTaskIndex = (this.currentTaskIndex + 1) % this.tasks.size();
        return this.tasks.get(nextTaskIndex);
    }

    /**
     * Moves the reference to the current task forward by one in the circular task list.
     * <p>
     * After calling this method, the current task should be the next task in the circular list
     * after the "old" current task.
     * <p>
     * Note that the list is treated as circular, so if the current task is the last in the list,
     * the new current task should be the first element of the list.
     * @ass1
     */
    public void moveToNextTask() {
        this.currentTaskIndex = (this.currentTaskIndex + 1) % this.tasks.size();
    }

    /**
     * Returns the human-readable string representation of this task list.
     * <p>
     * The format of the string to return is
     * <pre>TaskList currently on currentTask [taskNum/totalNumTasks]</pre>
     * where {@code currentTask} is the {@code toString()} representation of the current task as
     * returned by {@link Task#toString()},
     * {@code taskNum} is the place the current task occurs in the task list, and
     * {@code totalNumTasks} is the number of tasks in the task list.
     * <p>
     * For example, a task list with the list of tasks {@code [AWAY, LAND, WAIT, LOAD, TAKEOFF]}
     * which is currently on the {@code WAIT} task would have a string representation of
     * {@code "TaskList currently on WAIT [3/5]"}.
     *
     * @return string representation of this task list
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("TaskList currently on %s [%d/%d]",
                this.getCurrentTask(),
                this.currentTaskIndex + 1,
                this.tasks.size());
    }

    /**
     * Check whether each task in the task list is valid.
     *
     * @param current The current task to be validated
     * @param next The following task of the current task
     * @throws IllegalArgumentException if the task is invalid
     * @ass2
     */
    private void taskCheck(Task current, Task next) throws IllegalArgumentException {
        String errorMessage = "The given list of tasks is invalid";
        boolean check = false;
        TaskType currentType = current.getType();
        TaskType nextType = next.getType();
        if (currentType.equals(TaskType.AWAY)) {
            if (nextType.equals(TaskType.AWAY) || nextType.equals(TaskType.LAND)) {
                check = true;
            }
        } else if (currentType.equals(TaskType.LAND) || currentType.equals(TaskType.WAIT)) {
            if (nextType.equals(TaskType.WAIT) || nextType.equals(TaskType.LOAD)) {
                check = true;
            }
        } else if (currentType.equals(TaskType.LOAD)) {
            if (nextType.equals(TaskType.TAKEOFF)) {
                check = true;
            }
        } else if (currentType.equals(TaskType.TAKEOFF)) {
            if (nextType.equals(TaskType.AWAY)) {
                check = true;
            }
        }
        if (!check) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the machine-readable string representation of this task list.
     * The format of the string to return is
     * {@code encodedTask1,encodedTask2,...,encodedTaskN}
     * where {@code encodedTaskX} is the encoded representation of the Xth task in the task list,
     * for X between 1 and N inclusive, where N is the number of tasks in the task list
     * and {@code encodedTask1} represents <b>the current task</b>.
     *
     * For example, for a task list with 6 tasks and a current task of WAIT:
     * {@code WAIT,LOAD@75,TAKEOFF,AWAY,AWAY,LAND}
     * @return encoded string representation of this task list
     * @ass2
     */
    public String encode() {
        StringJoiner result = new StringJoiner(",");
        for (Task task : this.tasks) {
            result.add(task.encode());
        }
        return result.toString();
    }
}
