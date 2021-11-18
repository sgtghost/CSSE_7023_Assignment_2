package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.util.Encodable;

import java.util.List;
import java.util.StringJoiner;

/**
 * Abstract representation of a queue containing aircraft.
 * Aircraft can be added to the queue,
 * aircraft at the front of the queue can be queried or removed.
 * A list of all aircraft contained in the queue (in queue order) can be obtained.
 * The queue can be checked for containing a specified aircraft.
 *
 * The order that aircraft are removed from the queue depends on
 * the chosen concrete implementation of the AircraftQueue.
 *
 * @ass2
 */
public abstract class AircraftQueue implements Encodable {
    /**
     * Adds the given aircraft to the queue.
     * @param aircraft aircraft to add to queue
     */
    public abstract void addAircraft(Aircraft aircraft);

    /**
     * Removes and returns the aircraft at the front of the queue.
     * Returns null if the queue is empty.
     * @return aircraft at front of queue
     */
    public abstract Aircraft removeAircraft();

    /**
     * Returns the aircraft at the front of the queue without removing it from the queue,
     * or null if the queue is empty.
     * @return aircraft at front of queue
     */
    public abstract Aircraft peekAircraft();

    /**
     * Returns a list containing all aircraft in the queue, in order.
     * That is, the first element of the returned list should be the first aircraft that
     * would be returned by calling removeAircraft(), and so on.
     *
     * Adding or removing elements from the returned list should not affect the original queue.
     * @return list of all aircraft in queue, in queue order
     */
    public abstract List<Aircraft> getAircraftInOrder();

    /**
     * Returns true if the given aircraft is in the queue.
     * @param aircraft aircraft to find in queue
     * @return true if aircraft is in queue; false otherwise
     */
    public abstract boolean containsAircraft(Aircraft aircraft);

    /**
     * Returns the human-readable string representation of this aircraft queue.
     * The format of the string to return is
     * {@code QueueType [callsign1, callsign2, ..., callsignN]}
     * where {@code QueueType} is the concrete queue class (i.e. LandingQueue or TakeoffQueue)
     * and {@code callsign1} through {@code callsignN} are the callsigns
     * of all aircraft in the queue, in queue order (see {@link #getAircraftInOrder()}).
     *
     * For example, "{@code LandingQueue [ABC123, XYZ987, BOB555]}"
     * for a landing queue with three aircraft
     * and "{@code TakeoffQueue []}" for a takeoff queue with no aircraft.
     * <b>Hint:</b> {@code Object#getClass().getSimpleName()}
     * can be used to find the class name of an object.
     *
     * @return string representation of this queue
     */
    @Override
    public String toString() {
        String queueType = this.getClass().getSimpleName();

        StringJoiner callSigns = new StringJoiner(", ");
        String callSignList;

        List<Aircraft> aircrafts = this.getAircraftInOrder();
        if (aircrafts.size() != 0) {
            for (Aircraft aircraft : aircrafts) {
                callSigns.add(aircraft.getCallsign());
            }
            callSignList = callSigns.toString();
        } else {
            callSignList = "";
        }

        return String.format("%s [%s]",
                queueType,
                callSignList);
    }

    /**
     * Returns the machine-readable string representation of this aircraft queue.
     * The format of the string to return is
     * {@code QueueType:numAircraft}
     * {@code callsign1,callsign2,...,callsignN}
     * where:
     * <ul>
     * <li>{@code QueueType} is the simple class name of this queue, e.g. {@code LandingQueue}</li>
     * <li>{@code numAircraft} is the number of aircraft currently waiting in the queue</li>
     * <li>{@code callsignX} is the callsign of the Xth aircraft in the queue,
     * in the same order as returned by {@link #getAircraftInOrder()},
     * for X between 1 and N inclusive, where N is the number of aircraft in the queue</li>
     *
     * For example:
     * {@code LandingQueue:0}
     * For example:
     * {@code TakeoffQueue:3}
     * {@code ABC101,QWE456,XYZ789}
     * </ul>
     *
     * @return encoded string representation of this aircraft queue
     */
    public String encode() {
        int numAircraft = this.getAircraftInOrder().size();
        String headline = String.format("%s:%d",
                this.getClass().getSimpleName(),
                numAircraft
                );
        if (numAircraft == 0) {
            return headline;
        } else {
            StringJoiner callsigns = new StringJoiner(",");
            for (Aircraft aircraft : this.getAircraftInOrder()) {
                callsigns.add(aircraft.getCallsign());
            }
            String callsignLine = callsigns.toString();
            StringJoiner result = new StringJoiner(System.lineSeparator());
            result.add(headline);
            result.add(callsignLine);
            return result.toString();
        }
    }
}
