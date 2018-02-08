package xyz.a5s7.lift;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class LiftTest {
    @Test
    public void shouldNotMoveWhenCallTheSameFloor() throws InterruptedException {
        List<Event> expectedEventQ = new ArrayList<>();
        expectedEventQ.add(Event.Move(1));
        expectedEventQ.add(Event.Move(2));
        expectedEventQ.add(Event.DoorOpened(3));
        expectedEventQ.add(Event.DoorClosed());
        expectedEventQ.add(Event.Move(3));
        expectedEventQ.add(Event.Move(2));
        expectedEventQ.add(Event.DoorOpened(1));
        expectedEventQ.add(Event.DoorClosed());
        expectedEventQ.add(Event.DoorOpened(1));
        expectedEventQ.add(Event.DoorClosed());

        List<Event> eventQ = new ArrayList<>();

        Lift lift = createLift(
                eventQ,
                Settings.withTimeToNextFloor(3, 10, 0, 0)
        );

        lift.call(3);
        lift.call(1);
        lift.call(1);

        lift.stop(false);

        assertListEquals(expectedEventQ, eventQ);
    }

    @Test
    public void shouldMoveWithProperSpeed() throws InterruptedException {
        List<Event> expectedEventQ = new ArrayList<>();
        expectedEventQ.add(Event.Move(1));
        expectedEventQ.add(Event.Move(2));
        expectedEventQ.add(Event.DoorOpened(3));//2
        expectedEventQ.add(Event.DoorClosed());//3
        expectedEventQ.add(Event.Move(3));//4
        expectedEventQ.add(Event.Move(4));//5
        expectedEventQ.add(Event.Move(5));//6
        expectedEventQ.add(Event.Move(6));//7
        expectedEventQ.add(Event.DoorOpened(7));
        expectedEventQ.add(Event.DoorClosed());//9
        expectedEventQ.add(Event.Move(7));//10
        expectedEventQ.add(Event.Move(6));//11
        expectedEventQ.add(Event.Move(5));//12
        expectedEventQ.add(Event.Move(4));//13
        expectedEventQ.add(Event.Move(3));//14
        expectedEventQ.add(Event.Move(2));//15
        expectedEventQ.add(Event.DoorOpened(1));
        expectedEventQ.add(Event.DoorClosed());

        List<Event> eventQ = new ArrayList<>();
        Settings elevatorSettings = Settings.create(3, 10, 130, 1.5f);
        Lift lift = createLift(
                eventQ,
                elevatorSettings
        );

        lift.call(3);
        lift.call(7);
        TimeUnit.SECONDS.sleep(2);
        lift.go(1);

        lift.stop(false);

        assertListEquals(expectedEventQ, eventQ);
        //internal implementation uses Thread.sleep and it doesn't provide any guarantee on precise timing
        //this assertion may fail on some OS
        assertTimestamps(eventQ, elevatorSettings);
    }

    private void assertTimestamps(List<Event> eventQ, Settings elevatorSettings) {
        for (int i = 0; i < eventQ.size() - 1; i++) {
            Event cur = eventQ.get(i);
            Event next = eventQ.get(i + 1);

            switch (next.getType()) {
                case MOVE:
                    if (cur.getType() == Event.Type.MOVE) {
                        if (timeDiff(next, cur) < elevatorSettings.getMsToNextFloor()) {
                            fail("Time between floors must be (almost) " + elevatorSettings.getMsToNextFloor());
                        }
                    } else if (cur.getType() == Event.Type.DOOR_CLOSED) {
                        if (timeDiff(next, cur) > 10) {
                            fail("Time between closing doors and moving to next floor must be (almost) 0");
                        }
                    }
                    break;
                case DOOR_CLOSED:
                    if (timeDiff(next, cur) < elevatorSettings.getDoorTime()) {
                        fail("Time between opening/closing doors must be (almost) " + elevatorSettings.getDoorTime());
                    }
                    break;
                case DOOR_OPENED:
                    if (timeDiff(next, cur) < elevatorSettings.getMsToNextFloor()) {
                        fail("Time between floors must be (almost) " + elevatorSettings.getMsToNextFloor());
                    }
                    break;
            }
        }
    }

    private long timeDiff(Event a, Event b) {
        return a.getTimestamp() - b.getTimestamp();
    }

    private void assertListEquals(List<Event> expectedEventQ, List<Event> eventQ) {
        assertEquals(expectedEventQ.size(), eventQ.size());
        for (int i = 0; i < expectedEventQ.size(); i++) {
            assertEquals(expectedEventQ.get(i).getDesc(), eventQ.get(i).getDesc());
        }
    }

    @Test
    public void shouldJustOpenDoorsWhenNoMoves() throws InterruptedException {
        List<Event> expectedEventQ = new ArrayList<>();
        expectedEventQ.add(Event.DoorOpened(1));
        expectedEventQ.add(Event.DoorClosed());
        expectedEventQ.add(Event.DoorOpened(1));
        expectedEventQ.add(Event.DoorClosed());

        List<Event> eventQ = new ArrayList<>();
        Lift lift = createLift(
                eventQ,
                Settings.withTimeToNextFloor(3, 10, 0, 0)
        );

        lift.call(1);
        lift.go(1);

        lift.stop(false);

        assertListEquals(expectedEventQ, eventQ);
    }

    private Lift createLift(List<Event> eventQ, Settings elevatorSettings) {
        Lift lift = new Lift(elevatorSettings);
        lift.addEventListener(eventQ::add);
        lift.addEventListener(System.out::println);
        return lift;
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldComplainWhenCalledIncorrectFloor() {
        Lift lift = createLift(
                new ArrayList<>(),
                Settings.withTimeToNextFloor(3, 10, 0, 0)
        );
        lift.call(12);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldComplainWhenRequestedIncorrectFloor() {
        Lift lift = createLift(
                new ArrayList<>(),
                Settings.withTimeToNextFloor(3, 10, 0, 0)
        );
        lift.go(-1);
    }
}