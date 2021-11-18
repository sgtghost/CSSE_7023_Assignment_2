package towersim.control;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.ground.AirplaneTerminal;
import towersim.ground.Gate;
import towersim.ground.HelicopterTerminal;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class LandingQueueTest {
    private Aircraft passengerAircraft;
    private Aircraft emergencyPassengerAircraft;
    private Aircraft fuelPassengerAircraft;
    private Aircraft freightAircraft1;
    private Aircraft freightAircraft2;
    private Aircraft emergencyFreightAircraft;
    private Aircraft fuelFreightAircraft;



    @Before
    public void setup() {
        TaskList taskList = new TaskList(List.of(
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 100),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY)
        ));


        this.passengerAircraft = new PassengerAircraft("PAA001",
                AircraftCharacteristics.AIRBUS_A320,
                taskList,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity , 0);

        this.emergencyPassengerAircraft = new PassengerAircraft("EPA001",
                AircraftCharacteristics.AIRBUS_A320,
                taskList,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2, 0);
        this.emergencyPassengerAircraft.declareEmergency();

        this.fuelPassengerAircraft = new PassengerAircraft("FPA001",
                AircraftCharacteristics.AIRBUS_A320,
                taskList,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 10, 100);

        this.freightAircraft1 = new FreightAircraft("FAA001",
                AircraftCharacteristics.BOEING_747_8F,
                taskList,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity / 2, 0);

        this.freightAircraft2 = new FreightAircraft("FAA002",
                AircraftCharacteristics.BOEING_747_8F,
                taskList,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity / 2, 0);

        this.emergencyFreightAircraft = new FreightAircraft("EAF001",
                AircraftCharacteristics.SIKORSKY_SKYCRANE,
                taskList,
                AircraftCharacteristics.SIKORSKY_SKYCRANE.fuelCapacity / 2, 0);
        this.emergencyFreightAircraft.declareEmergency();

        this.fuelFreightAircraft = new FreightAircraft("FAF001",
                AircraftCharacteristics.SIKORSKY_SKYCRANE,
                taskList,
                AircraftCharacteristics.SIKORSKY_SKYCRANE.fuelCapacity / 10, 0);

    }

    @Test
    public void addAircraftTest() {
        LandingQueue landingQueue = new LandingQueue();
        //null Test
        landingQueue.addAircraft(null);
        PassengerAircraft result1 = (PassengerAircraft) landingQueue.peekAircraft();
        Assert.assertNull(result1);
        //passengerAircraft Test
        landingQueue.addAircraft(this.passengerAircraft);
        PassengerAircraft result2 = (PassengerAircraft) landingQueue.peekAircraft();
        Assert.assertEquals(this.passengerAircraft, result2);
    }

    @Test
    public void peekAircraftTest() {
        //empty list test
        LandingQueue landingQueue = new LandingQueue();
        Aircraft result1 = landingQueue.peekAircraft();
        Assert.assertNull(result1);
        //freightAircraft without any special conditions test
        landingQueue.addAircraft(this.freightAircraft1);
        landingQueue.addAircraft(this.freightAircraft2);
        FreightAircraft result2 = (FreightAircraft) landingQueue.peekAircraft();
        Assert.assertEquals(this.freightAircraft1, result2);
        //passengerAircraft test
        landingQueue.addAircraft(this.passengerAircraft);
        PassengerAircraft result3 = (PassengerAircraft) landingQueue.peekAircraft();
        Assert.assertEquals(this.passengerAircraft, result3);
        //fuelCritical Test
        landingQueue.addAircraft(this.fuelFreightAircraft);
        landingQueue.addAircraft(this.fuelPassengerAircraft);
        FreightAircraft result4 = (FreightAircraft) landingQueue.peekAircraft();
        Assert.assertEquals(this.fuelFreightAircraft, result4);
        //Emergency Test
        landingQueue.addAircraft(this.emergencyPassengerAircraft);
        landingQueue.addAircraft(this.emergencyFreightAircraft);
        PassengerAircraft result5 = (PassengerAircraft) landingQueue.peekAircraft();
        Assert.assertEquals(this.emergencyPassengerAircraft, result5);
    }

    @Test
    public void removeAircraftTest () {
        LandingQueue landingQueue = new LandingQueue();
        //empty list test
        Aircraft result1 = landingQueue.removeAircraft();
        Assert.assertNull(result1);
        //Build-up list test
        landingQueue.addAircraft(this.freightAircraft1);
        landingQueue.addAircraft(this.freightAircraft2);
        landingQueue.addAircraft(this.passengerAircraft);
        landingQueue.addAircraft(this.fuelFreightAircraft);
        landingQueue.addAircraft(this.fuelPassengerAircraft);
        landingQueue.addAircraft(this.emergencyPassengerAircraft);
        landingQueue.addAircraft(this.emergencyFreightAircraft);
        //remove emergency ones first
        PassengerAircraft result2 = (PassengerAircraft) landingQueue.removeAircraft();
        Assert.assertEquals(this.emergencyPassengerAircraft, result2);
        FreightAircraft result3 = (FreightAircraft) landingQueue.removeAircraft();
        Assert.assertEquals(this.emergencyFreightAircraft, result3);
        //Then the fuel critical ones
        FreightAircraft result4 = (FreightAircraft) landingQueue.removeAircraft();
        Assert.assertEquals(this.fuelFreightAircraft, result4);
        PassengerAircraft result5 = (PassengerAircraft) landingQueue.removeAircraft();
        Assert.assertEquals(this.fuelPassengerAircraft, result5);
        //Then the passengerAircraft
        PassengerAircraft result6 = (PassengerAircraft) landingQueue.removeAircraft();
        Assert.assertEquals(this.passengerAircraft, result6);
        //Finally the freightAircraft without any special conditions
        FreightAircraft result7 = (FreightAircraft) landingQueue.removeAircraft();
        Assert.assertEquals(this.freightAircraft1, result7);
        FreightAircraft result8 = (FreightAircraft) landingQueue.removeAircraft();
        Assert.assertEquals(this.freightAircraft2, result8);
    }

    @Test
    public void getAircraftInOrderTest() {
        LandingQueue landingQueue = new LandingQueue();
        //empty list test
        int result1 = landingQueue.getAircraftInOrder().size();
        Assert.assertEquals(0,result1);
        //Build-up list test
        landingQueue.addAircraft(this.freightAircraft1);
        landingQueue.addAircraft(this.freightAircraft2);
        landingQueue.addAircraft(this.passengerAircraft);
        landingQueue.addAircraft(this.fuelFreightAircraft);
        landingQueue.addAircraft(this.fuelPassengerAircraft);
        landingQueue.addAircraft(this.emergencyPassengerAircraft);
        landingQueue.addAircraft(this.emergencyFreightAircraft);

        List<Aircraft> result2 = landingQueue.getAircraftInOrder();

        Assert.assertEquals(this.emergencyPassengerAircraft,result2.get(0));
        Assert.assertEquals(this.emergencyFreightAircraft,result2.get(1));
        Assert.assertEquals(this.fuelFreightAircraft,result2.get(2));
        Assert.assertEquals(this.fuelPassengerAircraft,result2.get(3));
        Assert.assertEquals(this.passengerAircraft,result2.get(4));
        Assert.assertEquals(this.freightAircraft1,result2.get(5));
        Assert.assertEquals(this.freightAircraft2,result2.get(6));
    }

    @Test
    public void containsAircraftTest() {
        LandingQueue landingQueue = new LandingQueue();
        //empty list test
        Assert.assertFalse(landingQueue.containsAircraft(this.passengerAircraft));
        //Build-up list test
        landingQueue.addAircraft(this.freightAircraft1);
        landingQueue.addAircraft(this.freightAircraft2);
        landingQueue.addAircraft(this.passengerAircraft);
        landingQueue.addAircraft(this.fuelFreightAircraft);
        landingQueue.addAircraft(this.fuelPassengerAircraft);
        landingQueue.addAircraft(this.emergencyPassengerAircraft);
        landingQueue.addAircraft(this.emergencyFreightAircraft);

        Assert.assertTrue(landingQueue.containsAircraft(this.freightAircraft1));
        Assert.assertTrue(landingQueue.containsAircraft(this.freightAircraft2));
        Assert.assertTrue(landingQueue.containsAircraft(this.passengerAircraft));
        Assert.assertTrue(landingQueue.containsAircraft(this.fuelFreightAircraft));
        Assert.assertTrue(landingQueue.containsAircraft(this.fuelPassengerAircraft));
        Assert.assertTrue(landingQueue.containsAircraft(this.emergencyPassengerAircraft));
        Assert.assertTrue(landingQueue.containsAircraft(this.emergencyFreightAircraft));
    }


}
