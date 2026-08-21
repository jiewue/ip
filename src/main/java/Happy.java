import java.util.Scanner;

/**
 * Main entry point for the Happy chatbot application.
 * Handles task creation, listing, marking, and exception management.
 */
public class Happy {
    // Horizontal line separator used for formatting output blocks
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Starts the Happy chatbot program, reads user inputs, handles commands,
     * catches application exceptions, and exits on "bye".
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

        Task[] tasks = new Task[100];
        int taskCount = 0;

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
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i]);
                    }
                    System.out.println(DIVIDER);
                } else if (input.startsWith("mark") || input.equalsIgnoreCase("mark")) {
                    handleMarkCommand(input, tasks, taskCount);
                } else if (input.startsWith("unmark") || input.equalsIgnoreCase("unmark")) {
                    handleUnmarkCommand(input, tasks, taskCount);
                } else if (input.startsWith("todo") || input.equalsIgnoreCase("todo")) {
                    taskCount = handleTodoCommand(input, tasks, taskCount);
                } else if (input.startsWith("deadline") || input.equalsIgnoreCase("deadline")) {
                    taskCount = handleDeadlineCommand(input, tasks, taskCount);
                } else if (input.startsWith("event") || input.equalsIgnoreCase("event")) {
                    taskCount = handleEventCommand(input, tasks, taskCount);
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

    private static int handleTodoCommand(String input, Task[] tasks, int taskCount) throws HappyException {
        String description = input.length() > 4 ? input.substring(4).trim() : "";
        if (description.isEmpty()) {
            throw new HappyException("OOPS!!! The description of a todo cannot be empty.");
        }
        Task task = new Todo(description);
        tasks[taskCount] = task;
        taskCount++;
        printAddedTask(task, taskCount);
        return taskCount;
    }

    private static int handleDeadlineCommand(String input, Task[] tasks, int taskCount) throws HappyException {
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
        tasks[taskCount] = task;
        taskCount++;
        printAddedTask(task, taskCount);
        return taskCount;
    }

    private static int handleEventCommand(String input, Task[] tasks, int taskCount) throws HappyException {
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
        tasks[taskCount] = task;
        taskCount++;
        printAddedTask(task, taskCount);
        return taskCount;
    }

    private static void handleMarkCommand(String input, Task[] tasks, int taskCount) throws HappyException {
        String indexStr = input.length() > 4 ? input.substring(4).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("OOPS!!! Please specify a task number to mark.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            if (index < 0 || index >= taskCount) {
                throw new HappyException("OOPS!!! Task number " + (index + 1) + " does not exist.");
            }
            tasks[index].markAsDone();
            System.out.println(DIVIDER);
            System.out.println("Nice! I've marked this task as done:");
            System.out.println("  " + tasks[index]);
            System.out.println(DIVIDER);
        } catch (NumberFormatException e) {
            throw new HappyException("OOPS!!! Task number must be a valid integer.");
        }
    }

    private static void handleUnmarkCommand(String input, Task[] tasks, int taskCount) throws HappyException {
        String indexStr = input.length() > 6 ? input.substring(6).trim() : "";
        if (indexStr.isEmpty()) {
            throw new HappyException("OOPS!!! Please specify a task number to unmark.");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
            if (index < 0 || index >= taskCount) {
                throw new HappyException("OOPS!!! Task number " + (index + 1) + " does not exist.");
            }
            tasks[index].markAsNotDone();
            System.out.println(DIVIDER);
            System.out.println("OK, I've marked this task as not done yet:");
            System.out.println("  " + tasks[index]);
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
