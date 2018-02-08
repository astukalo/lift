package xyz.a5s7.lift;

import java.util.concurrent.TimeUnit;

public class Settings {
    public static final long ONE_SECOND_IN_MILLIS = TimeUnit.SECONDS.toMillis(1);
    private final float velocity; // in m/s
    private final int height; //in m
    private final int count;
    private final int doorTime; //in milliseconds
    private final int msToNextFloor;

    private Settings(float velocity, int height, int count, int doorTimeInMs) {
        this(velocity, height, count, doorTimeInMs, calcTimeToNextFloor(height, velocity));
    }

    private Settings(float velocity, int height, int count, int doorTimeInMs, int msToNextFloor) {
        if (count < 5 || count > 20) {
            throw new IllegalArgumentException("Number of floors must be between 5 and 20");
        }

        this.height = height;
        this.count = count;
        this.doorTime = doorTimeInMs;
        this.msToNextFloor = msToNextFloor;
        this.velocity = velocity;
    }

    private Settings(int height, int count, int doorTimeInMs, int msToNextFloor) {
        this(calcVelocity(height, msToNextFloor), height, count, doorTimeInMs, msToNextFloor);
    }

    public float getVelocity() {
        return velocity;
    }

    public int getHeight() {
        return height;
    }

    public int getCount() {
        return count;
    }

    /**
     * @return time in milliseconds
     */
    public int getDoorTime() {
        return doorTime;
    }

    public long getMsToNextFloor() {
        return msToNextFloor;
    }

    private static int calcTimeToNextFloor(int height, float velocity) {
        return Math.round(ONE_SECOND_IN_MILLIS * height / velocity);
    }

    private static float calcVelocity(int height, int msToNextFloor) {
        return 1.0f * ONE_SECOND_IN_MILLIS * height / msToNextFloor;
    }

    public static Settings create(int height, int number, int doorTimeInMs, float velocity) {
        return new Settings(velocity, height, number, doorTimeInMs);
    }

    public static Settings withTimeToNextFloor(int height, int number, int doorTimeInMs, int msToNextFloor) {
        return new Settings(height, number, doorTimeInMs, msToNextFloor);
    }

    public void checkFloor(int floor) {
        if (floor >= 1 && floor <= count) {
            return;
        }

        throw new IllegalArgumentException("Floor must be between 1 and " + count);
    }
}
