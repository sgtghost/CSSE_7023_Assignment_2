package towersim.control;

import towersim.aircraft.Aircraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a first-in-first-out (FIFO) queue of aircraft waiting to take off.
 * FIFO ensures that the order in which aircraft are allowed to take off is based on
 * how long they have been waiting in the queue.
 * An aircraft that has been waiting for longer than
 * another aircraft will always be allowed to take off before the other aircraft.
 *
 * @ass2
 */
public class TakeoffQueue extends AircraftQueue {

    /**List to store the queue of aircrafts */
    private List<Aircraft> queue;

    /**
     * Constructs a new TakeoffQueue with an initially empty queue of aircraft.
     * @ass2
     */
    public TakeoffQueue() {
        this.queue = new ArrayList<>();
    }

    /**
     * Adds the given aircraft to the queue.
     * @param aircraft aircraft to add to queue
     */
    @Override
    public void addAircraft(Aircraft aircraft) {
        if (aircraft != null) {
            queue.add(aircraft);
        }
    }

    /**
     * Returns the aircraft at the front of the queue
     * removing it from the queue, or null if the queue is empty.
     * Aircraft returned by {@code peekAircraft()} should be in the same order
     * that they were added via {@code addAircraft()}.
     * @return aircraft at front of queue
     */
    @Override
    public Aircraft peekAircraft() {
        if (queue.size() == 0) {
            return null;
        } else {
            return queue.get(0);
        }
    }

    /**
     * Removes and returns the aircraft at the front of the queue.
     * Returns null if the queue is empty.
     * Aircraft returned by {@code removeAircraft()} should be in the same order
     * that they were added via {@code addAircraft()}.
     * @return aircraft at front of queue
     */
    @Override
    public Aircraft removeAircraft() {
        Aircraft removal = this.peekAircraft();
        if (removal != null) {
            queue.remove(removal);
        }
        return removal;
    }

    /**
     * Returns a list containing all aircraft in the queue, in order.
     * That is, the first element of the returned list
     * should be the first aircraft that would be returned by calling {@code removeAircraft()},
     * and so on.
     * Adding or removing elements from the returned list should not affect the original queue.
     * @return list of all aircraft in queue, in queue order
     */
    @Override
    public List<Aircraft> getAircraftInOrder() {
        List<Aircraft> orderList = new ArrayList<>();
        TakeoffQueue newQueue = new TakeoffQueue();
        newQueue.queue = new ArrayList<>(this.queue);
        for (int i = 0; i < this.queue.size(); i++) {
            orderList.add(newQueue.removeAircraft());
        }
        return orderList;
    }

    /**
     * Returns true if the given aircraft is in the queue.
     * @param aircraft aircraft to find in queue
     * @return true if aircraft is in queue; false otherwise
     */
    @Override
    public boolean containsAircraft(Aircraft aircraft) {
        return queue.contains(aircraft);
    }
}
