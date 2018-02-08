package xyz.a5s7.lift;

import java.util.List;
import java.util.concurrent.*;

public class Lift {
    private final Settings settings;
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());//requests are not prioritized
    private final ThreadPoolExecutor notifecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());
    private final List<EventListener> eventListenerList = new CopyOnWriteArrayList<>();

    //primitive is enough, as we use single thread
    private int curFloor;

    public Lift(final Settings settings) {
        this.settings = settings;
        curFloor = 1;
    }

    /**
     * Puts 'Call' request into queue
     * @param floor
     */
    public void call(int floor) {
        settings.checkFloor(floor);
        processRequest(floor);
    }

    private void processRequest(int floor) {
        executor.submit(() -> {
            try {
                doMove(floor);
                openDoor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Puts 'Go' request into queue
     * @param floor
     */
    public void go(int floor) {
        settings.checkFloor(floor);
        processRequest(floor);
    }

    public boolean isDone() {
        return executor.getQueue().isEmpty() && executor.getActiveCount() == 0;
    }

    public void stop(boolean immediately) throws InterruptedException {
        if (immediately) {
            executor.shutdownNow();
        } else {
            executor.shutdown();
            while (!executor.awaitTermination(1, TimeUnit.SECONDS)) ;
        }
        notifecutor.shutdown();
        while (!notifecutor.awaitTermination(1, TimeUnit.SECONDS));
    }

    private void openDoor() throws InterruptedException {
        notifyAbout(Event.DoorOpened(curFloor));
        if (settings.getDoorTime() > 0) {
            TimeUnit.MILLISECONDS.sleep(settings.getDoorTime());
        }
        notifyAbout(Event.DoorClosed());
    }

    private void notifyAbout(Event event) {
        notifecutor.submit(
                () -> eventListenerList.forEach(
                        listener -> listener.onNewEvent(event)
                )
        );
    }

    private void doMove(int floor) throws InterruptedException {
        if (curFloor == floor) {
            return;
        }

        int step = curFloor < floor ? +1 : -1;

        while (curFloor != floor) {
            notifyAbout(Event.Move(curFloor));
            if (settings.getMsToNextFloor() > 0) {
                TimeUnit.MILLISECONDS.sleep(settings.getMsToNextFloor());
            }
            curFloor += step;
        }
    }

    public void addEventListener(EventListener listener) {
        eventListenerList.add(listener);
    }
}
