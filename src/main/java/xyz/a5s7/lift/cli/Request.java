package xyz.a5s7.lift.cli;

import java.util.Objects;

public class Request {
    public enum Type {
        CALL, GO, UNKNOWN
    }

    private final Type type;
    private final int floor;

    public Request(final Type type, final int floor) {
        this.type = type;
        this.floor = floor;
    }

    public Type getType() {
        return type;
    }

    public int getFloor() {
        return floor;
    }

    public static Request parse(String s) {
        if (s == null || "".equals(s)) {
            return new Request(Type.UNKNOWN, 0);
        }

        String[] in = s.trim().split("\\s+");
        if (in != null && in.length >=2) {
            try {
                if ("c".equalsIgnoreCase(in[0])) {
                    return new Request(Type.CALL, Integer.valueOf(in[1]));
                } else if ("g".equalsIgnoreCase(in[0])) {
                    return new Request(Type.GO, Integer.valueOf(in[1]));
                }
            } catch (NumberFormatException e) {
            }
        }

        return new Request(Type.UNKNOWN, 0);
    }

    private static Request newRequest(final Type type, int floor) {
        return new Request(type, floor);
    }

    public static Request CALL(final int floor) {
        return newRequest(Type.CALL, floor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return floor == request.floor &&
                type == request.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, floor);
    }
}