package towersim.control;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.PassengerAircraft;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;
import towersim.util.MalformedSaveException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ControlTowerInitialiserTest {
    private TaskList correctTaskList;
    private String correctlyEncodedTaskList;
    private String wrongType;
    private String notIntegerLoadPercentage;
    private String negativeLoadPercentage;
    private String multipleAT;
    private String wrongOrder;
    private String empty;
    private PassengerAircraft passengerAircraft;
    @Before
    public void setup () {
        this.correctlyEncodedTaskList = "LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY";
        this.correctTaskList = new TaskList(List.of(
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 100),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY)
        ));
        this.passengerAircraft = new PassengerAircraft("FPA001",
                AircraftCharacteristics.AIRBUS_A320,
                correctTaskList,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 10, 100);

        this.wrongType = "Land,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY";
        this.notIntegerLoadPercentage = "LAND,WAIT,WAIT,LOAD@35.4,TAKEOFF,AWAY,AWAY";
        this.negativeLoadPercentage = "LAND,WAIT,WAIT,LOAD@-50,TAKEOFF,AWAY,AWAY";
        this.multipleAT = "LAND,WAIT,WAIT,LOAD@@100,TAKEOFF,AWAY,AWAY";
        this.wrongOrder = "WAIT,LAND,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY";
        this.empty = "";
    }
    @Test
    public void readCorrectTaskListTest() {
        try {
            TaskList result = ControlTowerInitialiser.readTaskList(this.correctlyEncodedTaskList);
            Assert.assertEquals(this.correctlyEncodedTaskList, result.encode());
        } catch (MalformedSaveException ignored) {
            ;
        }
    }

    @Test(expected = MalformedSaveException.class)
    public void readWrongTypeTaskListTest() throws MalformedSaveException{
        TaskList result = ControlTowerInitialiser.readTaskList(this.wrongType);
    }

    @Test(expected = MalformedSaveException.class)
    public void readNotIntegerLoadPercentageTaskListTest() throws MalformedSaveException{
        TaskList result = ControlTowerInitialiser.readTaskList(this.notIntegerLoadPercentage);
    }

    @Test(expected = MalformedSaveException.class)
    public void readNegativeLoadPercentageTaskListTest() throws MalformedSaveException{
        TaskList result = ControlTowerInitialiser.readTaskList(this.negativeLoadPercentage);
    }

    @Test(expected = MalformedSaveException.class)
    public void readMultipleATTaskListTest() throws MalformedSaveException{
        TaskList result = ControlTowerInitialiser.readTaskList(this.multipleAT);
    }

    @Test(expected = MalformedSaveException.class)
    public void readWrongOrderTaskListTest() throws MalformedSaveException{
        TaskList result = ControlTowerInitialiser.readTaskList(this.wrongOrder);
    }

    @Test(expected = MalformedSaveException.class)
    public void readEmptyTaskListTest() throws MalformedSaveException{
        TaskList result = ControlTowerInitialiser.readTaskList(this.empty);
    }

    @Test
    public void readCorrectAircraftTest() {
        try {
            ControlTowerInitialiser.readAircraft(this.passengerAircraft.encode());
        } catch (MalformedSaveException ignored) {
            ;
        }
    }

    @Test(expected = MalformedSaveException.class)
    public void readIncorrectColonAircraftTest1() throws MalformedSaveException {
        String faulty = "FPA001:AIRBUS_A320:LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY:2720.00:false:100:";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }

    @Test(expected = MalformedSaveException.class)
    public void readIncorrectColonAircraftTest2() throws MalformedSaveException {
        String faulty = ":FPA001:AIRBUS_A320:LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY:2720.00:false:100";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }

    @Test(expected = MalformedSaveException.class)
    public void readIncorrectColonAircraftTest3() throws MalformedSaveException {
        String faulty = "FPA001AIRBUS_A320:LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY:2720.00:false:100";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }

    @Test(expected = MalformedSaveException.class)
    public void readIncorrectColonAircraftTest4() throws MalformedSaveException {
        String faulty = "FPA001AIRBUS_A320LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY2720.00false100";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }

    @Test(expected = MalformedSaveException.class)
    public void readInvalidCharacteristicsAircraftTest1() throws MalformedSaveException {
        String faulty = "FPA001:null:LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY:2720.00:false:100";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }

    @Test(expected = MalformedSaveException.class)
    public void readInvalidCharacteristicsAircraftTest2() throws MalformedSaveException {
        String faulty = "FPA001::LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY:2720.00:false:100";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }

    @Test(expected = MalformedSaveException.class)
    public void readInvalidCharacteristicsAircraftTest3() throws MalformedSaveException {
        String faulty = "FPA001:AIRBUS_A321:LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY:2720.00:false:100";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }

    @Test(expected = MalformedSaveException.class)
    public void readNoneDoubleFuelAircraftTest() throws MalformedSaveException {
        String faulty = "FPA001:AIRBUS_A320:LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY:2720X:false:100";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }

    @Test(expected = MalformedSaveException.class)
    public void readInvalidFuelAircraftTest1() throws MalformedSaveException {
        String faulty = "FPA001:AIRBUS_A320:LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY:-1000:false:100";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }

    @Test(expected = MalformedSaveException.class)
    public void readInvalidFuelAircraftTest2() throws MalformedSaveException {
        String faulty = "FPA001:AIRBUS_A320:LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY:300000:false:100";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }

    @Test(expected = MalformedSaveException.class)
    public void readNoneIntCargoAircraftTest() throws MalformedSaveException {
        String faulty = "FPA001:AIRBUS_A320:LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY:2720.00:false:100x";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }

    @Test(expected = MalformedSaveException.class)
    public void readInvalidCargoAircraftTest1() throws MalformedSaveException {
        String faulty = "FPA001:AIRBUS_A320:LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY:2720.00:false:500";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }
    @Test(expected = MalformedSaveException.class)
    public void readInvalidCargoAircraftTest2() throws MalformedSaveException {
        String faulty = "FPA001:AIRBUS_A320:LAND,WAIT,WAIT,LOAD@100,TAKEOFF,AWAY,AWAY:2720.00:false:-100";
        Aircraft result = ControlTowerInitialiser.readAircraft(faulty);
    }

    @Test
    public void loadCorrectAircraftTest() {
        String aircraft1 = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132";
        String aircraft2 = "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0";
        String aircraft3 = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0";
        String aircraft4 = "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4";
        String correctSave = String.join(
                System.lineSeparator(),
                "4", aircraft1, aircraft2, aircraft3, aircraft4);
        try {
            List<Aircraft> result = new ArrayList<>();
            result = ControlTowerInitialiser.loadAircraft(new StringReader(correctSave));
            Assert.assertEquals(result.get(0).encode(), aircraft1);
            Assert.assertEquals(result.get(1).encode(), aircraft2);
            Assert.assertEquals(result.get(2).encode(), aircraft3);
            Assert.assertEquals(result.get(3).encode(), aircraft4);
        } catch (Exception ignored) {
            ;
        }
    }
    @Test(expected = MalformedSaveException.class)
    public void loadNoneIntAircraftTest() throws MalformedSaveException{
        String aircraft1 = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132";
        String aircraft2 = "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0";
        String aircraft3 = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0";
        String aircraft4 = "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4";
        String correctSave = String.join(
                System.lineSeparator(),
                "4x", aircraft1, aircraft2, aircraft3, aircraft4);
        List<Aircraft> result = new ArrayList<>();
        try {
            result = ControlTowerInitialiser.loadAircraft(new StringReader(correctSave));
        } catch (IOException ignored) {
            ;
        }
    }
    @Test(expected = MalformedSaveException.class)
    public void loadIncorrectCountAircraftTest1() throws MalformedSaveException{
        String aircraft1 = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132";
        String aircraft2 = "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0";
        String aircraft3 = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0";
        String aircraft4 = "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4";
        String correctSave = String.join(
                System.lineSeparator(),
                "3", aircraft1, aircraft2, aircraft3, aircraft4);
        List<Aircraft> result = new ArrayList<>();
        try {
            result = ControlTowerInitialiser.loadAircraft(new StringReader(correctSave));
        } catch (IOException ignored) {
            ;
        }
    }
    @Test(expected = MalformedSaveException.class)
    public void loadIncorrectCountAircraftTest2() throws MalformedSaveException{
        String aircraft1 = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132";
        String aircraft2 = "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0";
        String aircraft3 = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0";
        String aircraft4 = "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4";
        String correctSave = String.join(
                System.lineSeparator(),
                "5", aircraft1, aircraft2, aircraft3, aircraft4);
        List<Aircraft> result = new ArrayList<>();
        try {
            result = ControlTowerInitialiser.loadAircraft(new StringReader(correctSave));
        } catch (IOException ignored) {
            ;
        }
    }
    @Test(expected = MalformedSaveException.class)
    public void loadIncorrectCountAircraftTest3() throws MalformedSaveException{
        String aircraft1 = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132";
        String aircraft2 = "UTD302:BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0";
        String aircraft3 = "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0";
        String aircraft4 = "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4";
        String correctSave = String.join(
                System.lineSeparator(),
                "0", aircraft1, aircraft2, aircraft3, aircraft4);
        List<Aircraft> result = new ArrayList<>();
        try {
            result = ControlTowerInitialiser.loadAircraft(new StringReader(correctSave));
        } catch (IOException ignored) {
            ;
        }
    }

    @Test(expected = MalformedSaveException.class)
    public void loadIncorrectCountAircraftTest4() throws MalformedSaveException{
        String aircraft1 = "";
        String aircraft2 = "";
        String aircraft3 = "";
        String aircraft4 = "";
        String correctSave = String.join(
                System.lineSeparator(),
                "3", aircraft1, aircraft2, aircraft3, aircraft4);
        List<Aircraft> result = new ArrayList<>();
        try {
            result = ControlTowerInitialiser.loadAircraft(new StringReader(correctSave));
        } catch (IOException ignored) {
            ;
        }
    }


}
