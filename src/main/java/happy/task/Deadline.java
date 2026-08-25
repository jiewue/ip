package happy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a Deadline task that needs to be done before a specific date/time.
 */
public class Deadline extends Task {
    protected String byRaw;
    protected LocalDate byDate;

    /**
     * Constructs a Deadline task with description and completion deadline.
     *
     * @param description Description of the task.
     * @param by Deadline date/time string.
     */
    public Deadline(String description, String by) {
        super(description);
        this.byRaw = by;
        this.byDate = parseDate(by);
    }

    /**
     * Attempts to parse a date string into a LocalDate object using standard formats.
     *
     * @param dateStr Raw date string input.
     * @return Parsed LocalDate if successful, or null if non-standard.
     */
    private static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
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
                // Continue trying remaining date formatters
            }
        }
        return null;
    }

    @Override
    public boolean isOccurringOn(LocalDate date) {
        return byDate != null && byDate.equals(date);
    }

    @Override
    public String toFileFormat() {
        String dateString = (byDate != null)
                ? byDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : byRaw;
        return "D | " + super.toFileFormat() + " | " + dateString;
    }

    @Override
    public String toString() {
        String formattedDate = (byDate != null)
                ? byDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
                : byRaw;
        return "[D]" + super.toString() + " (by: " + formattedDate + ")";
    }
}
