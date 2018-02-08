package xyz.a5s7.lift;

import java.util.Objects;

public class Event {
    public enum Type {
        MOVE, DOOR_CLOSED, DOOR_OPENED
    }

    private final Type type;
    private final String desc;
    private final long timestamp;

    public Event(Type type, String desc, long timestamp) {
        this.type = type;
        this.desc = desc;
        this.timestamp = timestamp;
    }

    public Event(Type type, String desc) {
        this(type, desc, System.currentTimeMillis());
    }

    public String getDesc() {
        return desc;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static Event Move(int floor) {
        return new Event(Type.MOVE, "Moving " + floor);
    }

    public static Event DoorOpened(int floor) {
        return new Event(Type.DOOR_OPENED, "Door opened at " + floor);
    }

    public static Event DoorClosed() {
        return new Event(Type.DOOR_CLOSED, "Door closed");
    }

    @Override
    public String toString() {
        return "Event{" +
                "desc='" + desc + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return timestamp == event.timestamp &&
                type == event.type &&
                Objects.equals(desc, event.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, desc, timestamp);
    }
}
