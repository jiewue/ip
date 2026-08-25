import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents an Event task occurring within a start and end time window.
 */
public class Event extends Task {
    protected String fromRaw;
    protected String toRaw;
    protected LocalDate fromDate;
    protected LocalDate toDate;

    /**
     * Constructs an Event task with description, start time, and end time.
     *
     * @param description Description of the event.
     * @param from Start time/date string.
     * @param to End time/date string.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.fromRaw = from;
        this.toRaw = to;
        this.fromDate = parseDate(from);
        this.toDate = parseDate(to);
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
        if (fromDate != null && toDate != null) {
            return !date.isBefore(fromDate) && !date.isAfter(toDate);
        }
        if (fromDate != null) {
            return fromDate.equals(date);
        }
        if (toDate != null) {
            return toDate.equals(date);
        }
        return false;
    }

    @Override
    public String toFileFormat() {
        String fromString = (fromDate != null)
                ? fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : fromRaw;
        String toString = (toDate != null)
                ? toDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : toRaw;
        return "E | " + super.toFileFormat() + " | " + fromString + " | " + toString;
    }

    @Override
    public String toString() {
        String formattedFrom = (fromDate != null)
                ? fromDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
                : fromRaw;
        String formattedTo = (toDate != null)
                ? toDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
                : toRaw;
        return "[E]" + super.toString() + " (from: " + formattedFrom + " to: " + formattedTo + ")";

    }
}
