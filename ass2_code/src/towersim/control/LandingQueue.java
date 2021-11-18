package towersim.control;

import towersim.aircraft.Aircraft;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a rule-based queue of aircraft waiting in the air to land.
 * The rules in the landing queue are designed to ensure that
 * aircraft are prioritised for landing based on "urgency" factors such as remaining fuel onboard,
 * emergency status and cargo type.
 *
 * @ass2
 */
public class LandingQueue extends AircraftQueue {
    /**
     * A list to store the aircraft in queue.
     */
    private List<Aircraft> queue;

    /**
     * Constructs a new LandingQueue with an initially empty queue of aircraft.
     * @ass2
     */
    public LandingQueue() {
        queue = new ArrayList<Aircraft>();
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
     * Returns the aircraft at the front of the queue without removing it from the queue,
     * or null if the queue is empty.
     * The rules for determining which aircraft in the queue should be returned next are as follows:
     * <ul>
     * <li>1. If an aircraft is currently in a state of emergency,
     * it should be returned. If more than one aircraft are in an emergency,
     * return the one added to the queue first.</li>
     * <li>2. If an aircraft has less than or equal to 20 percent fuel remaining,
     * a critical level, it should be returned (see {@link Aircraft#getFuelPercentRemaining()}).
     * If more than one aircraft have a critical level of fuel onboard,
     * return the one added to the queue first.</li>
     * <li>3. If there are any passenger aircraft in the queue,
     * return the passenger aircraft that was added to the queue first.</li>
     * <li>4. If this point is reached and no aircraft has been returned,
     * return the aircraft that was added to the queue first.</li>
     * </ul>
     * @return aircraft at front of queue
     */
    @Override
    public Aircraft peekAircraft() {
        if (queue.size() == 0) {
            return null;
        } else {
            for (Aircraft aircraft : queue) {
                if (aircraft.hasEmergency()) {
                    return aircraft;
                }
            }
            for (Aircraft aircraft : queue) {
                if (aircraft.getFuelPercentRemaining() <= 20) {
                    return aircraft;
                }
            }
            for (Aircraft aircraft : queue) {
                if (aircraft.getClass().getSimpleName().equals("PassengerAircraft")) {
                    return aircraft;
                }
            }
            return queue.get(0);
        }
    }

    /**
     * Removes and returns the aircraft at the front of the queue.
     * Returns null if the queue is empty.
     * The same rules as described in {@code peekAircraft()} should be used for determining
     * which aircraft to remove and return.
     * @return aircraft at front of queue
     */
    @Override
    public Aircraft removeAircraft() {
        Aircraft removal = peekAircraft();
        if (removal != null) {
            queue.remove(removal);
        }
        return removal;
    }


    /**
     * Returns a list containing all aircraft in the queue, in order.
     * That is, the first element of the returned list should be the first aircraft
     * that would be returned by calling {@code removeAircraft()}, and so on.
     *
     * Adding or removing elements from the returned list should not affect the original queue.
     * @return list of all aircraft in queue, in queue order
     */
    @Override
    public List<Aircraft> getAircraftInOrder() {
        List<Aircraft> orderList = new ArrayList<>();
        LandingQueue newQueue = new LandingQueue();
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
