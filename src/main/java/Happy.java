import java.util.Scanner;

/**
 * Main entry point for the Happy chatbot application.
 */
public class Happy {
    // Horizontal line separator used for formatting output blocks
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Starts the Happy chatbot program, stores tasks entered by the user,
     * lists them when requested, and exits when the user enters "bye".
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

        String[] tasks = new String[100];
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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
                System.out.println(DIVIDER);
            } else {
                tasks[taskCount] = input;
                taskCount++;
                System.out.println(DIVIDER);
                System.out.println("added: " + input);
                System.out.println(DIVIDER);
            }
        }
        scanner.close();
    }
}
