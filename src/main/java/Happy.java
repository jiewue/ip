import java.util.Scanner;

/**
 * Main entry point for the Happy chatbot application.
 */
public class Happy {
    // Horizontal line separator used for formatting output blocks
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Starts the Happy chatbot program, manages Task objects,
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
                tasks[taskCount] = new Task(input);
                taskCount++;
                System.out.println(DIVIDER);
                System.out.println("added: " + input);
                System.out.println(DIVIDER);
            }
        }
        scanner.close();
    }
}
