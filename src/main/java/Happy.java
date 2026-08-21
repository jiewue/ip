import java.util.Scanner;

/**
 * Main entry point for the Happy chatbot application.
 * Manages tasks including Todos, Deadlines, and Events.
 */
public class Happy {
    // Horizontal line separator used for formatting output blocks
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Starts the Happy chatbot program, manages Task objects (Todos, Deadlines, Events),
     * supports mark/unmark functionality, and exits when the user enters "bye".
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
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                System.out.println(DIVIDER);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(DIVIDER);
                break;
            } else if (input.equals("list")) {
                System.out.println(DIVIDER);
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + "." + tasks[i]);
                }
                System.out.println(DIVIDER);
            } else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring(5)) - 1;
                if (index >= 0 && index < taskCount) {
                    tasks[index].markAsDone();
                    System.out.println(DIVIDER);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + tasks[index]);
                    System.out.println(DIVIDER);
                }
            } else if (input.startsWith("unmark ")) {
                int index = Integer.parseInt(input.substring(7)) - 1;
                if (index >= 0 && index < taskCount) {
                    tasks[index].markAsNotDone();
                    System.out.println(DIVIDER);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + tasks[index]);
                    System.out.println(DIVIDER);
                }
            } else {
                Task task;
                if (input.startsWith("todo ")) {
                    String description = input.substring(5).trim();
                    task = new Todo(description);
                } else if (input.startsWith("deadline ")) {
                    String body = input.substring(9).trim();
                    int byIndex = body.indexOf("/by ");
                    if (byIndex != -1) {
                        String description = body.substring(0, byIndex).trim();
                        String by = body.substring(byIndex + 4).trim();
                        task = new Deadline(description, by);
                    } else {
                        task = new Deadline(body, "");
                    }
                } else if (input.startsWith("event ")) {
                    String body = input.substring(6).trim();
                    int fromIndex = body.indexOf("/from ");
                    int toIndex = body.indexOf("/to ");
                    if (fromIndex != -1 && toIndex != -1 && fromIndex < toIndex) {
                        String description = body.substring(0, fromIndex).trim();
                        String from = body.substring(fromIndex + 6, toIndex).trim();
                        String to = body.substring(toIndex + 4).trim();
                        task = new Event(description, from, to);
                    } else {
                        task = new Event(body, "", "");
                    }
                } else {
                    task = new Todo(input);
                }

                tasks[taskCount] = task;
                taskCount++;
                printAddedTask(task, taskCount);
            }
        }
        scanner.close();
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
