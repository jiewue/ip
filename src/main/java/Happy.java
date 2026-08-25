import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main entry point for the Happy chatbot application.
 * Handles task management (Todo, Deadline, Event), listing, marking, deleting,
 * file persistence (saving/loading), and custom exception handling.
 */
public class Happy {
    // Horizontal line separator used for formatting output blocks
    private static final String DIVIDER = "____________________________________________________________";

    // Path to the data storage file (OS-independent relative path)
    private static final String DATA_DIR = Paths.get(".", "data").toString();
    private static final String DATA_FILE = Paths.get(".", "data", "happy.txt").toString();

    /**
     * Starts the Happy chatbot program, loads existing tasks from disk,
     * processes user inputs, saves task updates, and exits on "bye".
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        String banner = "  _    _   _   ___  ___  __   __\n"
                + " | |  | | /_\\ | _ \\| _ \\ \\ \\ / /\n"
                + " | __ | |/ _ \\|  _/|  _/  \\ V / \n"
                + " |_||_|_/_/ \\_\\_|  |_|     |_|  \n";

        // Display greeting section
        System.out.println(DIVIDER);
        System.out.print(banner);
        System.out.println("Hello! I'm Happy.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);

        ArrayList<Task> tasks = new ArrayList<>();
        loadTasks(tasks);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            try {
                if (input.equalsIgnoreCase("bye")) {
                    System.out.println(DIVIDER);
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(DIVIDER);
                    break;
                } else if (input.equalsIgnoreCase("list")) {
                    System.out.println(DIVIDER);
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i));
                    }
                    System.out.println(DIVIDER);
                } else if (input.startsWith("delete") || input.equalsIgnoreCase("delete")) {
                    handleDeleteCommand(input, tasks);
                    saveTasks(tasks);
                } else if (input.startsWith("mark") || input.equalsIgnoreCase("mark")) {
                    handleMarkCommand(input, tasks);
                    saveTasks(tasks);
                } else if (input.startsWith("unmark") || input.equalsIgnoreCase("unmark")) {
                    handleUnmarkCommand(input, tasks);
                    saveTasks(tasks);
                } else if (input.startsWith("todo") || input.equalsIgnoreCase("todo")) {
                    handleTodoCommand(input, tasks);
                    saveTasks(tasks);
                } else if (input.startsWith("deadline") || input.equalsIgnoreCase("deadline")) {
                    handleDeadlineCommand(input, tasks);
                    saveTasks(tasks);
                } else if (input.startsWith("event") || input.equalsIgnoreCase("event")) {
                    handleEventCommand(input, tasks);
                    saveTasks(tasks);
                } else {
                    throw new HappyException("OOPS!!! I'm sorry, but I don't know what that means :-(");
                }
            } catch (HappyException e) {
                System.out.println(DIVIDER);
                System.out.println(" " + e.getMessage());
                System.out.println(DIVIDER);
            }
        }
        scanner.close();
    }

    /**
     * Loads tasks from the local hard disk file if it exists.
     * Skips corrupted lines safely.
     *
     * @param tasks The task list to populate with loaded tasks.
     */
    private static void loadTasks(ArrayList<Task> tasks) {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s*\\|\\s*");
                if (parts.length < 3) {
                    continue; // Skip invalid or corrupted format
                }

                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];

                Task task = null;
                if (type.equals("T")) {
                    task = new Todo(description);
                } else if (type.equals("D") && parts.length >= 4) {
                    String by = parts[3];
                    task = new Deadline(description, by);
                } else if (type.equals("E") && parts.length >= 5) {
                    String from = parts[3];
                    String to = parts[4];
                    task = new Event(description, from, to);
                }

                if (task != null) {
                    if (isDone) {
                        task.markAsDone();
                    }
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            System.out.println("Warning: Unable to load data from file: " + e.getMessage());
        }
    }

    /**
     * Saves the current list of tasks to the hard disk file.
     * Creates parent directories if they do not exist.
     *
     * @param tasks The task list to save.
     */
    private static void saveTasks(ArrayList<Task> tasks) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(DATA_FILE);
            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : tasks) {
                    writer.write(task.toFileFormat() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Unable to save data to file: " + e.getMessage());
        }
    }

    private static void handleDeleteCommand(String input, ArrayList<Task> tasks) throws HappyException {
        String indexStr = input.length() > 6 ? input.substring(6).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("OOPS!!! Please specify a task number to delete.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new HappyException("OOPS!!! Task number " + (index + 1) + " does not exist.");
            }
            Task removedTask = tasks.remove(index);
            System.out.println(DIVIDER);
            System.out.println("Noted. I've removed this task:");
            System.out.println("  " + removedTask);
            System.out.println("Now you have " + tasks.size() + " tasks in the list.");
            System.out.println(DIVIDER);
        } catch (NumberFormatException e) {
            throw new HappyException("OOPS!!! Task number must be a valid integer.");
        }
    }

    private static void handleTodoCommand(String input, ArrayList<Task> tasks) throws HappyException {
        String description = input.length() > 4 ? input.substring(4).trim() : "";
        if (description.isEmpty()) {
            throw new HappyException("OOPS!!! The description of a todo cannot be empty.");
        }
        Task task = new Todo(description);
        tasks.add(task);
        printAddedTask(task, tasks.size());
    }

    private static void handleDeadlineCommand(String input, ArrayList<Task> tasks) throws HappyException {
        String body = input.length() > 8 ? input.substring(8).trim() : "";
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
        printAddedTask(task, tasks.size());
    }

    private static void handleEventCommand(String input, ArrayList<Task> tasks) throws HappyException {
        String body = input.length() > 5 ? input.substring(5).trim() : "";
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
        printAddedTask(task, tasks.size());
    }

    private static void handleMarkCommand(String input, ArrayList<Task> tasks) throws HappyException {
        String indexStr = input.length() > 4 ? input.substring(4).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("OOPS!!! Please specify a task number to mark.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new HappyException("OOPS!!! Task number " + (index + 1) + " does not exist.");
            }
            tasks.get(index).markAsDone();
            System.out.println(DIVIDER);
            System.out.println("Nice! I've marked this task as done:");
            System.out.println("  " + tasks.get(index));
            System.out.println(DIVIDER);
        } catch (NumberFormatException e) {
            throw new HappyException("OOPS!!! Task number must be a valid integer.");
        }
    }

    private static void handleUnmarkCommand(String input, ArrayList<Task> tasks) throws HappyException {
        String indexStr = input.length() > 6 ? input.substring(6).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("OOPS!!! Please specify a task number to unmark.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new HappyException("OOPS!!! Task number " + (index + 1) + " does not exist.");
            }
            tasks.get(index).markAsNotDone();
            System.out.println(DIVIDER);
            System.out.println("OK, I've marked this task as not done yet:");
            System.out.println("  " + tasks.get(index));
            System.out.println(DIVIDER);
        } catch (NumberFormatException e) {
            throw new HappyException("OOPS!!! Task number must be a valid integer.");
        }
    }

    /**
     * Prints the confirmation message when a new task is added.
     *
     * @param task The task that was added.
     * @param taskCount Current total number of tasks in the list.
     */
    private static void printAddedTask(Task task, int taskCount) {
        System.out.println(DIVIDER);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(DIVIDER);
    }
}
