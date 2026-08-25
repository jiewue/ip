package happy.task;

import java.time.LocalDate;

/**
 * Represents a generic task with a description and completion status.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a new Task with the given description.
     *
     * @param description The task description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon of the task. "X" indicates done, " " indicates not done.
     *
     * @return Status icon string.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as incomplete.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the description of the task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if the task occurs or is due on a specific target date.
     * Base implementation returns false.
     *
     * @param date Target date to check against.
     * @return true if task occurs on target date, false otherwise.
     */
    public boolean isOccurringOn(LocalDate date) {
        return false;
    }

    /**
     * Converts the task object into a file storage string format.
     *
     * @return Formatted string representation for file storage.
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
