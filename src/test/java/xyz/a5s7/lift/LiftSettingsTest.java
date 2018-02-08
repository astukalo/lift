package xyz.a5s7.lift;

import org.junit.Test;

import static org.junit.Assert.*;

public class LiftSettingsTest {
    @Test
    public void shouldReturnValidTimeToNextFloor() {
        Settings elevatorSettings = Settings.create(1, 10, 1, 2.2f);
        assertEquals(455, elevatorSettings.getMsToNextFloor());
    }
}