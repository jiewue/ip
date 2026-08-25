package happy.task;

import java.time.LocalDate;
import java.util.ArrayList;

import happy.exception.HappyException;

/**
 * Encapsulates the task list and provides operations to add, delete,
 * mark, unmark, and query tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList with an existing list of tasks.
     *
     * @param tasks Initial list of tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a new task to the task list.
     *
     * @param task Task object to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes a task at the specified 0-based index.
     *
     * @param index Index of task to delete.
     * @return The deleted Task object.
     * @throws HappyException If the index is out of bounds.
     */
    public Task delete(int index) throws HappyException {
        if (index < 0 || index >= tasks.size()) {
            throw new HappyException("OOPS!!! Task number " + (index + 1) + " does not exist.");
        }
        return tasks.remove(index);
    }

    /**
     * Marks a task at the specified 0-based index as done.
     *
     * @param index Index of task to mark.
     * @return The marked Task object.
     * @throws HappyException If the index is out of bounds.
     */
    public Task mark(int index) throws HappyException {
        if (index < 0 || index >= tasks.size()) {
            throw new HappyException("OOPS!!! Task number " + (index + 1) + " does not exist.");
        }
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task at the specified 0-based index as not done.
     *
     * @param index Index of task to unmark.
     * @return The unmarked Task object.
     * @throws HappyException If the index is out of bounds.
     */
    public Task unmark(int index) throws HappyException {
        if (index < 0 || index >= tasks.size()) {
            throw new HappyException("OOPS!!! Task number " + (index + 1) + " does not exist.");
        }
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Retrieves the task at the specified 0-based index.
     *
     * @param index Index of task to retrieve.
     * @return Task object at index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the total number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying ArrayList of Task objects.
     *
     * @return ArrayList containing tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Finds and returns all tasks that occur on the specified target date.
     *
     * @param date Target date.
     * @return List of matching tasks.
     */
    public ArrayList<Task> getTasksOccurringOn(LocalDate date) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isOccurringOn(date)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
}
