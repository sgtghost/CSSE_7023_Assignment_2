package towersim.aircraft;

import towersim.tasks.TaskList;
import towersim.tasks.TaskType;

/**
 * Represents an aircraft capable of carrying freight cargo.
 * @ass1
 */
public class FreightAircraft extends Aircraft {

    /** Amount of freight currently onboard, in kilograms */
    private int freightAmount;

    /**
     * Creates a new freight aircraft with the given callsign, task list, fuel capacity, amount of
     * fuel and kilograms of freight.
     * <p>
     * If the given amount of freight is less than zero or greater than the aircraft's maximum
     * freight capacity as defined in the aircraft's characteristics, then an
     * {@code IllegalArgumentException} should be thrown.
     *
     * @param callsign        unique callsign
     * @param characteristics characteristics that describe this aircraft
     * @param tasks           task list to be used by aircraft
     * @param fuelAmount      current amount of fuel onboard, in litres
     * @param freightAmount   current amount of freight onboard, in kilograms
     * @throws IllegalArgumentException if freightAmount &lt; 0 or if freightAmount &gt; freight
     * capacity
     * @ass1
     */
    public FreightAircraft(String callsign, AircraftCharacteristics characteristics,
            TaskList tasks, double fuelAmount, int freightAmount) {
        super(callsign, characteristics, tasks, fuelAmount);

        if (freightAmount < 0) {
            throw new IllegalArgumentException("Amount of freight onboard cannot be negative");
        }
        if (freightAmount > characteristics.freightCapacity) {
            throw new IllegalArgumentException("Amount of freight onboard cannot exceed freight "
                    + "capacity");
        }

        this.freightAmount = freightAmount;
    }

    /**
     * Returns the total weight of the aircraft in its current state.
     * <p>
     * The total weight for a freight aircraft is calculated as the sum of:
     * <ul>
     * <li>the aircraft's empty weight</li>
     * <li>the amount of fuel onboard the aircraft multiplied by the weight of a litre of fuel</li>
     * <li>the weight of the aircraft's freight onboard</li>
     * </ul>
     *
     * @return total weight of aircraft in kilograms
     * @ass1
     */
    @Override
    public double getTotalWeight() {
        return super.getTotalWeight() + this.freightAmount;
    }

    /**
     * Returns the number of ticks required to load the aircraft at the gate.
     * <p>
     * The loading time for freight aircraft is given by the following table:
     * <table border="1"><caption>Freight loading time table</caption>
     * <tr><th>Freight to be loaded (kg)</th>
     * <th>Loading time (ticks)</th>
     * </tr>
     * <tr><td>&lt;1000</td><td>1</td></tr>
     * <tr><td>1000 to 50,000</td><td>2</td></tr>
     * <tr><td>&gt;50,000</td><td>3</td></tr>
     * </table>
     * <p>
     * The freight to be loaded is equal to the maximum freight capacity of the
     * aircraft multiplied by the load ratio specified in the aircraft's current task (see
     * {@link towersim.tasks.Task#getLoadPercent()}). The result of this calculation should be
     * rounded to the nearest whole kilogram.
     *
     * @return loading time in ticks
     * @ass1
     */
    @Override
    public int getLoadingTime() {
        int freightToLoad = this.getFreightToLoad();

        if (freightToLoad < 1000) {
            return 1;
        } else if (freightToLoad <= 50000) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Returns the ratio of freight cargo onboard to maximum available freight capacity as a
     * percentage between 0 and 100.
     * <p>
     * 0 represents no freight onboard, and 100 represents the aircraft being at maximum capacity
     * of freight onboard.
     * <p>
     * The calculated value should be rounded to the nearest percentage point.
     *
     * @return occupancy level as a percentage
     * @ass1
     */
    @Override
    public int calculateOccupancyLevel() {
        return (int) Math.round((double) this.freightAmount * 100
                / this.getCharacteristics().freightCapacity);
    }

    /**
     * Returns the total amount of freight to be loaded onto the aircraft based on the current
     * task's load percentage.
     *
     * @return total freight to be loaded, in kilograms
     * @ass1
     */
    private int getFreightToLoad() {
        int freightCapacity = this.getCharacteristics().freightCapacity;
        double loadRatio = (double) this.getTaskList().getCurrentTask().getLoadPercent() / 100;
        return (int) Math.round(freightCapacity * loadRatio);
    }

    /**
     * Updates the aircraft's state on each tick of the simulation.
     * <p>
     * Firstly, the {@link Aircraft#tick()} method in the superclass should be called to perform
     * refueling and burning of fuel.
     * <p>
     * Next, if the aircraft's current task is a {@code LOAD} task, freight should be loaded onto
     * the aircraft. The amount of freight to load in a single call of {@code tick()} is equal to
     * the total amount of freight to be loaded based on the {@code LOAD} task's load percentage,
     * divided by the loading time given by {@link #getLoadingTime()}. This ensures that freight
     * is loaded in equal increments across the entire loading time. The result of this division
     * operation may yield a freight amount that is not an integer, in which case it should be
     * rounded to the nearest whole integer (kilogram).
     * <p>
     * Note that the total amount of freight on the aircraft should not be allowed to exceed the
     * maximum freight capacity of the aircraft, given by
     * {@link AircraftCharacteristics#freightCapacity}.
     * <p>
     * For example, suppose an aircraft initially has 0kg of freight onboard and has a current task
     * of type {@code LOAD} with a load percentage of 65%. The aircraft has a freight capacity of
     * 40,000kg. Then, the total amount of freight to be loaded is 65% of 40,000kg = 26,000kg.
     * According to {@link #getLoadingTime()}, this amount of freight will take 2 ticks to load.
     * So, a single call to {@code tick()} should increase the amount of freight onboard by
     * 26,000kg / 2 = 13,000kg.
     * @ass1
     */
    @Override
    public void tick() {
        super.tick();

        if (this.getTaskList().getCurrentTask().getType() == TaskType.LOAD) {
            int freightToLoadThisTick = (int) Math.round(this.getFreightToLoad()
                    / (double) this.getLoadingTime());
            this.freightAmount = Math.min(this.freightAmount + freightToLoadThisTick,
                    this.getCharacteristics().freightCapacity);
        }
    }

    /**
     * Unloads the aircraft of all cargo (passengers/freight) it is currently carrying.
     * This action should be performed instantly. After calling unload(),
     * {@link #calculateOccupancyLevel()} should return 0 to indicate that the aircraft is empty.
     * @ass2
     */
    public void unload() {
        this.freightAmount = 0;
    }

    /**
     * Returns the machine-readable string representation of this aircraft.
     * The format of the string to return is
     *{@code callsign:model:taskListEncoded:fuelAmount:emergency:freightAmount}
     * where:
     * <ul>
     * <li>{@code callsign} is the aircraft's callsign</li>
     * <li>{@code model} is the {@code Enum.name()} of the aircraft's AircraftCharacteristics</li>
     * <li>{@code taskListEncoded} is the {@code encode()} representation of
     * the aircraft's task list (see @link #TaskList.encode())</li>
     * <li>{@code fuelAmount} is the aircraft's current amount of fuel onboard,
     * formatted to <b>exactly two (2)</b> decimal places</li>
     * <li>{@code emergency} is whether or not the aircraft is currently
     * in a state of emergency</li>
     * <li>{@code freightAmount} is the amount of freight currently onboard</li>
     * </ul>
     *
     * For example:
     * {@code ABC123:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,LOAD@50,TAKEOFF,AWAY:3250.00:false:86300}
     * @return encoded string representation of this aircraft
     * @ass2
     */
    @Override
    public String encode() {
        return String.format("%s:%d",
                super.encode(),
                this.freightAmount);
    }
}
