/**
 * Represents an Event task occurring within a start and end time window.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Constructs an Event task with description, start time, and end time.
     *
     * @param description Description of the event.
     * @param from Start time/date string.
     * @param to End time/date string.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from + " | " + to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
