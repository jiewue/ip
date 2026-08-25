package happy.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import happy.exception.HappyException;
import happy.task.Deadline;
import happy.task.Event;
import happy.task.Task;
import happy.task.TaskList;
import happy.task.Todo;

/**
 * Handles loading tasks from file and saving tasks to file on disk.
 */
public class Storage {
    private final String filePath;

    /**
     * Constructs a Storage manager for the specified file path.
     *
     * @param filePath Relative or absolute path to the data storage file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks from disk. If file or parent directory does not exist,
     * returns an empty task list. Skips corrupted lines safely.
     *
     * @return List of tasks loaded from file.
     * @throws HappyException If reading file encounters severe errors.
     */
    public ArrayList<Task> load() throws HappyException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return tasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s*\\|\\s*");
                if (parts.length < 3) {
                    continue; // Skip invalid or corrupted file format lines
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
            throw new HappyException("Warning: Failed to load task data from " + filePath);
        }

        return tasks;
    }

    /**
     * Saves the current list of tasks to file on disk.
     * Automatically creates parent directories if they do not exist.
     *
     * @param taskList TaskList containing tasks to persist.
     * @throws HappyException If writing to file fails.
     */
    public void save(TaskList taskList) throws HappyException {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                for (Task task : taskList.getTasks()) {
                    writer.write(task.toFileFormat() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new HappyException("Error: Failed to save tasks to file: " + e.getMessage());
        }
    }
}
