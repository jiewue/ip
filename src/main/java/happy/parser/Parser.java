package happy.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import happy.exception.HappyException;
import happy.storage.Storage;
import happy.task.Deadline;
import happy.task.Event;
import happy.task.Task;
import happy.task.TaskList;
import happy.task.Todo;
import happy.ui.Ui;

/**
 * Handles parsing user command strings and executing corresponding actions.
 */
public class Parser {

    /**
     * Parses the user command input and executes the appropriate operation on TaskList, Ui, and Storage.
     *
     * @param fullCommand Full user command string input.
     * @param tasks TaskList object containing current tasks.
     * @param ui Ui object for displaying messages.
     * @param storage Storage object for persisting tasks.
     * @return true if command is "bye" (exit signal), false otherwise.
     * @throws HappyException If command syntax or arguments are invalid.
     */
    public static boolean parseAndExecute(String fullCommand, TaskList tasks, Ui ui, Storage storage)
            throws HappyException {
        if (fullCommand.equalsIgnoreCase("bye")) {
            ui.showGoodbye();
            return true;
        } else if (fullCommand.equalsIgnoreCase("list")) {
            ui.showTaskList(tasks);
            return false;
        } else if (fullCommand.startsWith("date") || fullCommand.startsWith("on")) {
            handleDate(fullCommand, tasks, ui);
            return false;
        } else if (fullCommand.startsWith("delete") || fullCommand.equalsIgnoreCase("delete")) {
            handleDelete(fullCommand, tasks, ui, storage);
            return false;
        } else if (fullCommand.startsWith("mark") || fullCommand.equalsIgnoreCase("mark")) {
            handleMark(fullCommand, tasks, ui, storage);
            return false;
        } else if (fullCommand.startsWith("unmark") || fullCommand.equalsIgnoreCase("unmark")) {
            handleUnmark(fullCommand, tasks, ui, storage);
            return false;
        } else if (fullCommand.startsWith("todo") || fullCommand.equalsIgnoreCase("todo")) {
            handleTodo(fullCommand, tasks, ui, storage);
            return false;
        } else if (fullCommand.startsWith("deadline") || fullCommand.equalsIgnoreCase("deadline")) {
            handleDeadline(fullCommand, tasks, ui, storage);
            return false;
        } else if (fullCommand.startsWith("event") || fullCommand.equalsIgnoreCase("event")) {
            handleEvent(fullCommand, tasks, ui, storage);
            return false;
        } else if (fullCommand.startsWith("find") || fullCommand.equalsIgnoreCase("find")) {
            handleFind(fullCommand, tasks, ui);
            return false;
        } else {
            throw new HappyException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }

    private static void handleFind(String command, TaskList tasks, Ui ui) throws HappyException {
        String keyword = command.length() > 4 ? command.substring(4).trim() : "";
        if (keyword.isEmpty()) {
            throw new HappyException("OOPS!!! Please specify a keyword to search for.");
        }
        ui.showFoundTasks(tasks.find(keyword));
    }

    private static void handleTodo(String command, TaskList tasks, Ui ui, Storage storage) throws HappyException {
        String description = command.length() > 4 ? command.substring(4).trim() : "";
        if (description.isEmpty()) {
            throw new HappyException("OOPS!!! The description of a todo cannot be empty.");
        }
        Task task = new Todo(description);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private static void handleDeadline(String command, TaskList tasks, Ui ui, Storage storage) throws HappyException {
        String body = command.length() > 8 ? command.substring(8).trim() : "";
        if (body.isEmpty()) {
            throw new HappyException("OOPS!!! The description of a deadline cannot be empty.");
        }
        int byIndex = body.indexOf("/by");
        if (byIndex == -1) {
            throw new HappyException("OOPS!!! A deadline task must include a '/by' specified date/time.");
        }
        String description = body.substring(0, byIndex).trim();
        String by = body.substring(byIndex + 3).trim();
        if (description.isEmpty()) {
            throw new HappyException("OOPS!!! The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new HappyException("OOPS!!! The deadline date/time ('/by') cannot be empty.");
        }
        Task task = new Deadline(description, by);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private static void handleEvent(String command, TaskList tasks, Ui ui, Storage storage) throws HappyException {
        String body = command.length() > 5 ? command.substring(5).trim() : "";
        if (body.isEmpty()) {
            throw new HappyException("OOPS!!! The description of an event cannot be empty.");
        }
        int fromIndex = body.indexOf("/from");
        int toIndex = body.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
            throw new HappyException("OOPS!!! An event task must specify both '/from' and '/to' time frames.");
        }
        String description = body.substring(0, fromIndex).trim();
        String from = body.substring(fromIndex + 5, toIndex).trim();
        String to = body.substring(toIndex + 3).trim();
        if (description.isEmpty()) {
            throw new HappyException("OOPS!!! The description of an event cannot be empty.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new HappyException("OOPS!!! The event start ('/from') and end ('/to') times cannot be empty.");
        }
        Task task = new Event(description, from, to);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }

    private static void handleDelete(String command, TaskList tasks, Ui ui, Storage storage) throws HappyException {
        String indexStr = command.length() > 6 ? command.substring(6).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("OOPS!!! Please specify a task number to delete.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            Task removedTask = tasks.delete(index);
            storage.save(tasks);
            ui.showTaskRemoved(removedTask, tasks.size());
        } catch (NumberFormatException e) {
            throw new HappyException("OOPS!!! Task number must be a valid integer.");
        }
    }

    private static void handleMark(String command, TaskList tasks, Ui ui, Storage storage) throws HappyException {
        String indexStr = command.length() > 4 ? command.substring(4).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("OOPS!!! Please specify a task number to mark.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            Task markedTask = tasks.mark(index);
            storage.save(tasks);
            ui.showTaskMarked(markedTask);
        } catch (NumberFormatException e) {
            throw new HappyException("OOPS!!! Task number must be a valid integer.");
        }
    }

    private static void handleUnmark(String command, TaskList tasks, Ui ui, Storage storage) throws HappyException {
        String indexStr = command.length() > 6 ? command.substring(6).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("OOPS!!! Please specify a task number to unmark.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            Task unmarkedTask = tasks.unmark(index);
            storage.save(tasks);
            ui.showTaskUnmarked(unmarkedTask);
        } catch (NumberFormatException e) {
            throw new HappyException("OOPS!!! Task number must be a valid integer.");
        }
    }

    private static void handleDate(String command, TaskList tasks, Ui ui) throws HappyException {
        int spaceIndex = command.indexOf(' ');
        if (spaceIndex == -1 || spaceIndex == command.length() - 1) {
            throw new HappyException("OOPS!!! Please specify a date (e.g. date 2019-12-02 or date 2/12/2019).");
        }
        String dateStr = command.substring(spaceIndex + 1).trim();
        LocalDate targetDate = parseInputDate(dateStr);
        if (targetDate == null) {
            throw new HappyException(
                    "OOPS!!! Invalid date format. Please use yyyy-MM-dd or d/M/yyyy (e.g., 2019-12-02).");
        }
        ui.showTasksOnDate(targetDate, tasks.getTasksOccurringOn(targetDate));
    }

    private static LocalDate parseInputDate(String dateStr) {
        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("MMM dd yyyy"),
            DateTimeFormatter.ofPattern("MMM d yyyy")
        };
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr.trim(), formatter);
            } catch (DateTimeParseException ignored) {
                // Continue trying remaining formatters
            }
        }
        return null;
    }
}
