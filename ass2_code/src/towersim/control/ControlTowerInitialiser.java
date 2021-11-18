package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.ground.AirplaneTerminal;
import towersim.ground.Gate;
import towersim.ground.HelicopterTerminal;
import towersim.ground.Terminal;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;
import towersim.util.MalformedSaveException;
import towersim.util.NoSpaceException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;

/**
 * Utility class that contains static methods for loading a control tower
 * and associated entities from files.
 */
public class ControlTowerInitialiser {
    /**
     * Loads the number of ticks elapsed from the given reader instance.
     * The contents of the reader should match the format specified in
     * the tickWriter row of in the table shown in
     * {@link towersim.display.ViewModel#saveAs(Writer, Writer, Writer, Writer)}.
     *
     * For an example of valid tick reader contents, see the provided
     * {@code saves/tick_basic.txt} and {@code saves/tick_default.txt} files.
     *
     * The contents read from the reader are invalid if any of the following conditions are true:
     * <ul>
     * <li>The number of ticks elapsed is not an integer
     * (i.e. cannot be parsed by {@code Long.parseLong(String)}).</li>
     * <li>The number of ticks elapsed is less than zero.</li>
     * </ul>
     * @param reader reader from which to load the number of ticks elapsed
     * @return number of ticks elapsed
     * @throws MalformedSaveException if the format of the text read from the reader
     * is invalid according to the rules above
     * @throws IOException if an IOException is encountered when reading from the reader
     */
    public static long loadTick(Reader reader) throws MalformedSaveException, IOException {
        long tick;
        try (BufferedReader tickReader = new BufferedReader(reader)) {
            String line = tickReader.readLine();
            try {
                tick = Long.parseLong(line);
                if (tick < 0) {
                    throw new MalformedSaveException(
                            "The number of ticks elapsed is less than zero.");
                }
            } catch (NumberFormatException e) {
                throw new MalformedSaveException("The number of ticks elapsed is not an integer.");
            }
        }
        return tick;
    }

    /**
     * Loads the list of all aircraft managed by the control tower from the given reader instance.
     * The contents of the reader should match the format specified in
     * the aircraftWriter row of in the table shown in
     * {@link towersim.display.ViewModel#saveAs(Writer, Writer, Writer, Writer)}.
     *
     * For an example of valid aircraft reader contents,
     * see the provided {@code saves/aircraft_basic.txt} and
     * {@code saves/aircraft_default.txt} files.
     *
     * The contents read from the reader are invalid if any of the following conditions are true:
     * <ul>
     * <li>The number of aircraft specified on the first line of the reader is not an integer
     * (i.e. cannot be parsed by {@code Integer.parseInt(String)}).</li>
     * <li>The number of aircraft specified on the first line is not equal to
     * the number of aircraft actually read from the reader.</li>
     * <li>Any of the conditions listed in the Javadoc for
     * {@code readAircraft(String)} are true.</li>
     * </ul>
     * This method should call {@code readAircraft(String)}.
     * @param reader reader from which to load the list of aircraft
     * @return list of aircraft read from the reader
     * @throws IOException if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is
     * invalid according to the rules above
     */
    public static List<Aircraft> loadAircraft(Reader reader) throws IOException,
            MalformedSaveException {
        List<Aircraft> result = new ArrayList<>();
        try (BufferedReader aircraftReader = new BufferedReader(reader)) {
            int assertCount;
            int actualCount = 0;

            String line = aircraftReader.readLine();
            assertCount = Integer.parseInt(line);
            while ((line = aircraftReader.readLine()) != null) {
                actualCount += 1;
                result.add(readAircraft(line));
            }
            if (assertCount != actualCount) {
                throw new MalformedSaveException(
                        "The number of aircraft specified"
                                + "on the first line is not equal to"
                                + "the number of aircraft actually read from the reader.");
            }
        } catch (NumberFormatException e) {
            throw new MalformedSaveException(
                    "The number of aircraft specified"
                            + "on the first line of the reader is not an integer.");
        }
        return result;
    }



    /**
     * Loads the takeoff queue,
     * landing queue and map of loading aircraft from the given reader instance.
     * Rather than returning a list of queues, this method does not return anything.
     * Instead, it should <b>modify</b> the given takeoff queue,
     * landing queue and loading map by adding aircraft, etc.
     * The contents of the reader should match the format specified
     * in the queuesWriter row of in the table shown in
     * {@link towersim.display.ViewModel#saveAs(Writer, Writer, Writer, Writer)}.
     *
     * For an example of valid queues reader contents,
     * see the provided {@code saves/queues_basic.txt} and {@code saves/queues_default.txt} files.
     *
     * The contents read from the reader are invalid
     * if any of the conditions listed in the Javadoc for
     * {@link #readQueue(BufferedReader, List, AircraftQueue)}
     * and {@link #readLoadingAircraft(BufferedReader, List, Map)} are true.
     *
     * This method should call {@link #readQueue(BufferedReader, List, AircraftQueue)}
     * and {@link #readLoadingAircraft(BufferedReader, List, Map)}.
     * @param reader  reader from which to load the queues and loading map
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @param takeoffQueue empty takeoff queue that aircraft will be added to
     * @param landingQueue empty landing queue that aircraft will be added to
     * @param loadingAircraft empty map that aircraft and loading times will be added to
     * @throws MalformedSaveException  if the format of the text read from the reader is invalid
     * according to the rules above
     * @throws IOException if an IOException is encountered when reading from the reader
     */
    public static void loadQueues(
            Reader reader,
            List<Aircraft> aircraft,
            TakeoffQueue takeoffQueue,
            LandingQueue landingQueue,
            Map<Aircraft, Integer> loadingAircraft)
            throws MalformedSaveException, IOException {
        try (BufferedReader queuesReader = new BufferedReader(reader)) {
            readQueue(queuesReader, aircraft, takeoffQueue);
            readQueue(queuesReader, aircraft, landingQueue);
            readLoadingAircraft(queuesReader, aircraft, loadingAircraft);
        }
    }

    /**
     * Loads the list of terminals and their gates from the given reader instance.
     * The contents of the reader should match
     * the format specified in the terminalsWithGatesWriter row of in the table shown in
     * {@link towersim.display.ViewModel#saveAs(Writer, Writer, Writer, Writer)}.
     *
     * For an example of valid queues reader contents,
     * see the provided {@code saves/terminalsWithGates_basic.txt}
     * and {@code saves/terminalsWithGates_default.txt} files.
     *
     * The contents read from the reader are invalid if any of the following conditions are true:
     * <ul>
     * <li>The number of terminals specified at the top of the file is not an integer
     * (i.e. cannot be parsed by {@code Integer.parseInt(String)}).</li>
     * <li>The number of terminals specified is not equal to
     * the number of terminals actually read from the reader.</li>
     * <li>Any of the conditions listed in the Javadoc for
     * readTerminal(String, BufferedReader, List)
     * and readGate(String, List) are true.</li>
     * </ul>
     * This method should call readTerminal(String, BufferedReader, List).
     * @param reader reader from which to load the list of terminals and their gates
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @return list of terminals (with their gates) read from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     * according to the rules above
     * @throws IOException if an IOException is encountered when reading from the reader
     */
    public static List<Terminal> loadTerminalsWithGates(Reader reader,
                                                        List<Aircraft> aircraft)
            throws MalformedSaveException, IOException {
        try (BufferedReader terminals = new BufferedReader(reader)) {
            List<Terminal> result = new ArrayList<>();
            int assertCount;
            int actualCount = 0;
            String line = terminals.readLine();
            assertCount = Integer.parseInt(line);
            while ((line = terminals.readLine()) != null) {
                actualCount += 1;
                result.add(readTerminal(line, terminals, aircraft));
            }
            if (assertCount != actualCount) {
                throw new MalformedSaveException(
                        "The number of terminals specified is not equal to"
                                + "the number of terminals actually read from the reader.");
            }
            return result;
        } catch (NumberFormatException e) {
            throw new MalformedSaveException(
                    "The number of terminals specified at the top of the file is not an integer.");
        }
    }

    /**
     * Reads an aircraft from its encoded representation in the given string.
     * If the {@code AircraftCharacteristics.passengerCapacity} of
     * the encoded aircraft is greater than zero,
     * then a PassengerAircraft should be created and returned.
     * Otherwise, a FreightAircraft should be created and returned.
     *
     * The format of the string should match the encoded representation of an aircraft,
     * as described in {@link Aircraft#encode()}.
     *
     * The encoded string is invalid if any of the following conditions are true:
     * <ul>
     * <li>More/fewer colons (:) are detected in the string than expected.</li>
     * <li>The aircraft's AircraftCharacteristics is not valid,
     * i.e. it is not one of those listed in {@link AircraftCharacteristics#values()}.</li>
     * <li>The aircraft's fuel amount is not a double
     * (i.e. cannot be parsed by {@code Double.parseDouble(String)}).</li>
     * <li>The aircraft's fuel amount is less than zero or greater than
     * the aircraft's maximum fuel capacity.</li>
     * <li>The amount of cargo (freight/passengers) onboard the aircraft
     * is not an integer (i.e. cannot be parsed by {@code Integer.parseInt(String)}).</li>
     * <li>The amount of cargo (freight/passengers) onboard the aircraft is less than
     * zero or greater than the aircraft's maximum freight/passenger capacity.</li>
     * <li>Any of the conditions listed in the Javadoc for
     * {@link ControlTowerInitialiser#readTaskList(String)} are true.</li>
     * </ul>
     * This method should call {@link ControlTowerInitialiser#readTaskList(String)}.
     * @param line line of text containing the encoded aircraft
     * @return decoded aircraft instance
     * @throws MalformedSaveException if the format of the given string is invalid
     * according to the rules above
     */
    public static Aircraft readAircraft(String line) throws MalformedSaveException {
        Aircraft result;
        boolean freightOrNot = false;
        //check colons
        String[] info = line.split(":", 7);
        if (info.length != 6) {
            throw new MalformedSaveException("More/fewer colons (:)"
                    + "are detected in the string than expected.");
        }

        //callsign
        String callsign = info[0];

        AircraftCharacteristics characteristics;
        //check AircraftCharacteristics
        try {
            characteristics = AircraftCharacteristics.valueOf(info[1]);
        } catch (Exception e) {
            throw new MalformedSaveException(
                    "The aircraft's AircraftCharacteristics is not valid.");
        }

        double fuel;
        //check fuel amount
        try {
            fuel = Double.parseDouble(info[3]);
            double maxFuel = characteristics.fuelCapacity;
            if (fuel < 0 || fuel > maxFuel) {
                throw new MalformedSaveException("The aircraft's fuel amount is less than zero"
                        + "or greater than the aircraft's maximum fuel capacity.");
            }
        } catch (NumberFormatException e) {
            throw new MalformedSaveException("The aircraft's fuel amount is not a double.");
        }

        int cargo;
        //check cargo amount
        try {
            cargo = Integer.parseInt(info[5]);
            int cargoLimit = Math.max(characteristics.freightCapacity,
                    characteristics.passengerCapacity);
            if (cargoLimit == characteristics.freightCapacity) {
                freightOrNot = true;
            }
            if (cargo < 0 || cargo > cargoLimit) {
                throw new MalformedSaveException("The amount of cargo onboard the aircraft"
                        + "is less than zero or greater than"
                        + "the aircraft's maximum freight/passenger capacity.");
            }
        } catch (NumberFormatException e) {
            throw new MalformedSaveException("The amount of cargo onboard the aircraft"
                    + "is not an integer.");
        }

        //read taskList
        TaskList taskList = readTaskList(info[2]);

        boolean emergency;
        //check emergency
        try {
            emergency = Boolean.getBoolean(info[4]);
        } catch (Exception e) {
            throw new MalformedSaveException("The emergency state is invalid.");
        }
        if (freightOrNot) {
            result = new FreightAircraft(callsign, characteristics, taskList, fuel, cargo);
        } else {
            result = new PassengerAircraft(callsign, characteristics, taskList, fuel, cargo);
        }
        return result;
    }

    /**
     * Reads a task list from its encoded representation in the given string.
     * The format of the string should match the encoded representation of a task list,
     * as described in {@link TaskList#encode()}.
     *
     * The encoded string is invalid if any of the following conditions are true:
     * <ul>
     * <li>The task list's TaskType is not valid
     * (i.e. it is not one of those listed in {@link TaskType#values()}).</li>
     * <li>A task's load percentage is not an integer
     * (i.e. cannot be parsed by {@code Integer.parseInt(String)}).</li>
     * <li>A task's load percentage is less than zero.</li>
     * <li>More than one at-symbol (@) is detected for any task in the task list.</li>
     * <li>The task list is invalid according to the rules specified in
     * {@link TaskList#TaskList(List)}.</li>
     * </ul>
     * @param taskListPart string containing the encoded task list
     * @return decoded task list instance
     * @throws MalformedSaveException if the format of the given string is invalid
     * according to the rules above
     */
    public static TaskList readTaskList(String taskListPart) throws MalformedSaveException {
        String[] taskString = taskListPart.split(",");
        List<Task> tasks = new ArrayList<>();
        for (String task : taskString) {
            try {
                TaskType type;
                int loadPercentage = 0;
                String[] loadCheck = task.split("@", 3);
                if (loadCheck.length == 1) {
                    type = TaskType.valueOf(task);
                } else if (loadCheck.length == 2) {
                    type = TaskType.valueOf(loadCheck[0]);
                    loadPercentage = Integer.parseInt(loadCheck[1]);
                    if (loadPercentage < 0) {
                        throw new MalformedSaveException(
                                "A task's load percentage is less than zero.");
                    }
                } else {
                    throw new MalformedSaveException("More than one at-symbol (@)"
                            + "is detected for any task in the task list.");
                }
                Task result = new Task(type, loadPercentage);
                tasks.add(result);
            } catch (NumberFormatException e) {
                throw new MalformedSaveException("A task's load percentage is not an integer.");
            } catch (IllegalArgumentException e) {
                throw new MalformedSaveException("The task list's TaskType is not valid.");
            }
        }
        try {
            return new TaskList(tasks);
        } catch (IllegalArgumentException e) {
            throw new MalformedSaveException("The task list is invalid"
                    + "according to the rules specified in TaskList(List).");
        }
    }

    /**
     * Reads an aircraft queue from the given reader instance.
     * Rather than returning a queue, this method does not return anything.
     * Instead, it should <b>modify</b> the given aircraft queue by adding aircraft to it.
     *
     * The contents of the text read from the reader should match
     * the encoded representation of an aircraft queue,
     * as described in {@link AircraftQueue#encode()}.
     *
     * The contents read from the reader are invalid if any of the following conditions are true:
     * <ul>
     * <li>The first line read from the reader is null.</li>
     * <li>The first line contains more/fewer colons (:) than expected.</li>
     * <li>The queue type specified in the first line is not equal to
     * the simple class name of the queue provided as a parameter.</li>
     * <li>The number of aircraft specified on the first line is not an integer
     * (i.e. cannot be parsed by {@code Integer.parseInt(String)}).</li>
     * <li>The number of aircraft specified is greater than zero
     * and the second line read is null.</li>
     * <li>The number of callsigns listed on the second line is not equal to
     * the number of aircraft specified on the first line.</li>
     * <li>A callsign listed on the second line does not correspond to the callsign of
     * any aircraft contained in the list of aircraft given as a parameter.</li>
     * </ul>
     * @param reader reader from which to load the aircraft queue
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @param queue empty queue that aircraft will be added to
     * @throws IOException if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     * according to the rules above
     */
    public static void readQueue(BufferedReader reader,
                                 List<Aircraft> aircraft,
                                 AircraftQueue queue)
            throws IOException, MalformedSaveException {
        String line = reader.readLine();
        int assertCount;
        int actualCount = 0;

        if (line == null) {
            throw new MalformedSaveException("The first line read from the reader is null.");
        } else {
            String[] parts = line.split(":", 3);
            if (parts.length != 2) {
                throw new MalformedSaveException("The first line contains"
                        + "more/fewer colons (:) than expected.");
            } else {
                if (!parts[0].equals(queue.getClass().getSimpleName())) {
                    throw new MalformedSaveException("The queue type specified"
                            + "in the first line is not equal to"
                            + "the simple class name of the queue provided as a parameter.");
                }
                try {
                    assertCount = Integer.parseInt(parts[1]);
                    if (assertCount != 0) {
                        if (assertCount > 0 && (line = reader.readLine()) == null) {
                            throw new MalformedSaveException("The number of aircraft specified"
                                    + "is greater than zero and the second line read is null.");
                        }
                        String[] callsignParts = line.split(",");
                        actualCount = callsignParts.length;
                        if (actualCount != assertCount) {
                            throw new MalformedSaveException("The number of callsigns listed"
                                    + "on the second line is not equal to"
                                    + "the number of aircraft specified on the first line.");
                        }
                        for (String callsign : callsignParts) {
                            boolean callsignInList = false;
                            for (Aircraft individualAircraft : aircraft) {
                                if (individualAircraft.getCallsign().equals(callsign)) {
                                    callsignInList = true;
                                    queue.addAircraft(individualAircraft);
                                }
                            }
                            if (!callsignInList) {
                                throw new MalformedSaveException("A callsign listed on"
                                        + "the second line does not correspond to"
                                        + "the callsign of any aircraft contained in "
                                        + "the list of aircraft given as a parameter.");
                            }
                        }
                    }

                } catch (NumberFormatException e) {
                    throw new MalformedSaveException("The number of aircraft specified"
                            + "on the first line is not an integer.");
                }
            }

        }
    }

    /**
     * Reads the map of currently loading aircraft from the given reader instance.
     * Rather than returning a map, this method does not return anything.
     * Instead,
     * it should <b>modify</b> the given map by adding entries (aircraft/integer pairs) to it.
     *
     * The contents of the text read from the reader should match
     * the format specified in the queuesWriter row of in the table shown in
     * {@link towersim.display.ViewModel#saveAs(Writer, Writer, Writer, Writer)}.
     * Note that this method should only read the map of loading aircraft,
     * not the takeoff queue or landing queue.
     * Reading these queues is handled in the readQueue(BufferedReader, List, AircraftQueue) method.
     *
     * For an example of valid encoded map of loading aircraft,
     * see the provided {@code saves/queues_basic.txt}
     * and {@code saves/queues_default.txt files}.
     *
     * The contents read from the reader are invalid if any of the following conditions are true:
     * <ul>
     * <li>The first line read from the reader is null.</li>
     * <li>The number of colons (:) detected on the first line is more/fewer than expected.</li>
     * <li>The number of aircraft specified on the first line is not an integer
     * (i.e. cannot be parsed by {@code Integer.parseInt(String)}).</li>
     * <li>The number of aircraft is greater than zero
     * and the second line read from the reader is null.</li>
     * <li>The number of aircraft specified on the first line is not equal to
     * the number of callsigns read on the second line.</li>
     * <li>For any callsign/loading time pair on the second line,
     * the number of colons detected is not equal to one.
     * For example, {@code ABC123:5:9} is invalid.</li>
     * <li>A callsign listed on the second line does not correspond to
     * the callsign of any aircraft contained in the list of aircraft given as a parameter.</li>
     * <li>Any ticksRemaining value on the second line is not an integer
     * (i.e. cannot be parsed by {@code Integer.parseInt(String)}).</li>
     * <li>Any {@code ticksRemaining} value on the second line is less than one (1).</li>
     * </ul>
     * @param reader reader from which to load the map of loading aircraft
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @param loadingAircraft empty map that aircraft and their loading times will be added to
     * @throws IOException if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     * according to the rules above
     */
    public static void readLoadingAircraft(BufferedReader reader,
                                           List<Aircraft> aircraft,
                                           Map<Aircraft, Integer> loadingAircraft)
            throws IOException, MalformedSaveException {
        String line = reader.readLine();
        if (line == null) {
            throw new MalformedSaveException("The first line read from the reader is null.");
        }

        String[] firstLineParts = line.split(":", 3);
        if (firstLineParts.length != 2) {
            throw new MalformedSaveException("The number of colons (:) detected"
                    + "on the first line is more/fewer than expected.");
        }
        int assertCount;
        int actualCount = 0;
        try {
            assertCount = Integer.parseInt(firstLineParts[1]);
        } catch (NumberFormatException e) {
            throw new MalformedSaveException("The number of aircraft specified"
                    + "on the first line is not an integer.");
        }
        if (assertCount != 0) {
            line = reader.readLine();
            if (assertCount > 0 && line == null) {
                throw new MalformedSaveException("The number of aircraft is greater than zero"
                        + "and the second line read from the reader is null.");
            }
            String[] loadingParts = line.split(",");
            actualCount = loadingParts.length;
            if (actualCount != assertCount) {
                throw new MalformedSaveException("The number of aircraft specified"
                        + "on the first line is not equal to"
                        + "the number of callsigns read on the second line.");
            }
            for (String encodedLoadingAircraft : loadingParts) {
                String[] individual = encodedLoadingAircraft.split(":", 3);
                if (individual.length != 2) {
                    throw new MalformedSaveException("For any callsign/loading time pair"
                            + "on the second line,"
                            + "the number of colons detected is not equal to one.");
                }

                int ticksRemaining;
                try {
                    ticksRemaining = Integer.parseInt(individual[1]);
                    if (ticksRemaining < 1) {
                        throw new MalformedSaveException("ticksRemaining value"
                                + "on the second line is less than one.");
                    }
                } catch (NumberFormatException e) {
                    throw new MalformedSaveException("TicksRemaining value on"
                            + "the second line is not an integer.");
                }

                String callsign = individual[0];
                boolean callsignInList = false;
                for (Aircraft aircraftInList : aircraft) {
                    if (aircraftInList.getCallsign().equals(callsign)) {
                        callsignInList = true;
                        loadingAircraft.put(aircraftInList, ticksRemaining);
                    }
                }
                if (!callsignInList) {
                    throw new MalformedSaveException("A callsign listed on the second line"
                            + "does not correspond to the callsign of any aircraft"
                            + "contained in the list of aircraft given as a parameter.");
                }

            }

        }

    }

    /**
     * Reads a terminal from the given string and reads its gates from the given reader instance.
     * The format of the given string and the text read from the reader should match
     * the encoded representation of a terminal, as described in Terminal.encode().
     *
     * For an example of valid encoded terminal with gates,
     * see the provided {@code saves/terminalsWithGates_basic.txt}
     * and {@code saves/terminalsWithGates_default.txt} files.
     *
     * The encoded terminal is invalid if any of the following conditions are true:
     * <ul>
     * <li>The number of colons (:) detected on the first line is more/fewer than expected.</li>
     * <li>The terminal type specified on the first line is neither
     * {@code AirplaneTerminal} nor {@code HelicopterTerminal}.</li>
     * <li>The terminal number is not an integer
     * (i.e. cannot be parsed by {@code Integer.parseInt(String)}).</li>
     * <li>The terminal number is less than one (1).</li>
     * <li>The number of gates in the terminal is not an integer.</li>
     * <li>The number of gates is less than zero or is greater than
     * {@link Terminal#MAX_NUM_GATES}</li>
     * <li>A line containing an encoded gate was expected,
     * but EOF (end of file) was received
     * (i.e. {@code BufferedReader.readLine()} returns null).</li>
     * <li>Any of the conditions listed in the Javadoc for
     * {@link #readGate(String, List)} are true.</li>
     * </ul>
     * @param line string containing the first line of the encoded terminal
     * @param reader reader from which to load the gates of the terminal (subsequent lines)
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @return decoded terminal with its gates added
     * @throws IOException if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the given string or the text read
     * from the reader is invalid according to the rules above
     */
    public static Terminal readTerminal(
            String line, BufferedReader reader, List<Aircraft> aircraft)
            throws IOException, MalformedSaveException {
        Terminal result;
        String[] terminalInfo = line.split(":", 5);
        if (terminalInfo.length != 4) {
            throw new MalformedSaveException("The number of colons (:) detected"
                    + "on the first line is more/fewer than expected.");
        }

        int terminalNum;
        try {
            terminalNum = Integer.parseInt(terminalInfo[1]);
            if (terminalNum < 1) {
                throw new MalformedSaveException("The terminal number is less than one (1).");
            }
        } catch (NumberFormatException e) {
            throw new MalformedSaveException("The terminal number is not an integer.");
        }

        String type = terminalInfo[0];
        if (type.equals("HelicopterTerminal")) {
            result = new HelicopterTerminal(terminalNum);
        } else if (type.equals("AirplaneTerminal")) {
            result = new AirplaneTerminal(terminalNum);
        } else {
            throw new MalformedSaveException("The terminal type specified"
                    + "on the first line is neither AirplaneTerminal nor HelicopterTerminal.");
        }



        boolean emergency;
        try {
            emergency = Boolean.getBoolean(terminalInfo[2]);
            if (emergency) {
                result.hasEmergency();
            } else {
                result.clearEmergency();
            }
        } catch (SecurityException e) {
            throw new MalformedSaveException("The emergency state is not correct.");
        }

        int gateCount;
        try {
            gateCount = Integer.parseInt(terminalInfo[3]);
            if (gateCount < 0 || gateCount > Terminal.MAX_NUM_GATES) {
                throw new MalformedSaveException("The number of gates is less than zero"
                        + "or is greater than Terminal.MAX_NUM_GATES.");
            }
        } catch (NumberFormatException e) {
            throw new MalformedSaveException("The number of gates in the terminal"
                    + "is not an integer.");
        }


        for (int i = 0; i < gateCount; i++) {
            line = reader.readLine();
            if (line == null) {
                throw new MalformedSaveException("A line containing an encoded gate was expected,"
                        + "but EOF was received.");
            }
            Gate gate = readGate(line, aircraft);
            try {
                result.addGate(gate);
            } catch (NoSpaceException ignored) {
                ;
            }
        }
        return result;
    }

    /**
     * Reads a gate from its encoded representation in the given string.
     * The format of the string should match the encoded representation of a gate,
     * as described in {@link Gate#encode()}.
     *
     * The encoded string is invalid if any of the following conditions are true:
     * <ul>
     * <li>The number of colons (:) detected was more/fewer than expected.</li>
     * <li>The gate number is not an integer
     * (i.e. cannot be parsed by {@code Integer.parseInt(String)}).</li>
     * <li>The gate number is less than one (1).</li>
     * <li>The callsign of the aircraft parked at the gate is not {@code empty}
     * and the callsign does not correspond to the callsign of
     * any aircraft contained in the list of aircraft given as a parameter.</li>
     * </ul>
     * @param line string containing the encoded gate
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @return decoded gate instance
     * @throws MalformedSaveException if the format of the given string is invalid
     * according to the rules above
     */
    public static Gate readGate(String line, List<Aircraft> aircraft)
            throws MalformedSaveException {
        Gate result;
        String[] gateParts = line.split(":", 3);
        if (gateParts.length != 2) {
            throw new MalformedSaveException("The number of colons (:) detected"
                    + "was more/fewer than expected.");
        }
        int gateNum;
        try {
            gateNum = Integer.parseInt(gateParts[0]);
            if (gateNum < 1) {
                throw new MalformedSaveException("The gate number is less than one (1).");
            }
        } catch (NumberFormatException e) {
            throw new MalformedSaveException("The gate number is not an integer.");
        }
        result = new Gate(gateNum);


        String callsign = gateParts[1];
        if (!callsign.equals("empty")) {
            boolean callsignInList = false;
            for (Aircraft aircraftInList : aircraft) {
                if (aircraftInList.getCallsign().equals(callsign)) {
                    callsignInList = true;
                    try {
                        result.parkAircraft(aircraftInList);
                    } catch (NoSpaceException ignored) {
                        ;
                    }
                }
            }
            if (!callsignInList) {
                throw new MalformedSaveException("The callsign of the aircraft parked at the gate"
                        + "is not empty and the callsign does not correspond to"
                        + "the callsign of any aircraft contained in the list of aircraft"
                        + "given as a parameter.");
            }
        }
        return result;
    }

    /**
     * Creates a control tower instance by reading various airport entities from the given readers.
     * The following methods should be called in this order,
     * and their results stored temporarily, to load information from the readers:
     * <ul>
     * <li>{@link #loadTick(Reader)} to load the number of elapsed ticks</li>
     * <li>{@link #loadAircraft(Reader)} to load the list of all aircraft</li>
     * <li>{@link #loadTerminalsWithGates(Reader, List)} to load the terminals and their gates</li>
     * <li>{@link #loadQueues(Reader, List, TakeoffQueue, LandingQueue, Map)}
     * to load the takeoff queue,
     * landing queue and map of loading aircraft to their loading time remaining</li>
     * </ul>
     * <b>Note:</b> before calling
     * {@link #loadQueues(Reader, List, TakeoffQueue, LandingQueue, Map)},
     * an empty takeoff queue and landing queue should be created
     * by calling their respective constructors.
     * Additionally, an empty map should be created by calling:
     * {@code new TreeMap<>(Comparator.comparing(Aircraft::getCallsign))}
     * This is important as it will ensure that the map is ordered
     * by aircraft callsign (lexicographically).
     * Once all information has been read from the readers,
     * a new control tower should be initialised
     * by calling {@link ControlTower#ControlTower(long, List, LandingQueue, TakeoffQueue, Map)}
     * Finally, the terminals that have been read should be added to the control tower
     * by calling {@link ControlTower#addTerminal(Terminal)}
     * @param tick reader from which to load the number of ticks elapsed
     * @param aircraft reader from which to load the list of aircraft
     * @param queues reader from which to load the aircraft queues and map of loading aircraft
     * @param terminalsWithGates reader from which to load the terminals and their gates
     * @return control tower created by reading from the given readers
     * @throws MalformedSaveException if reading from any of the given readers results in
     * a MalformedSaveException, indicating the contents of that reader are invalid
     * @throws IOException if an IOException is encountered when reading from any of the readers
     */
    public static ControlTower createControlTower(
            Reader tick, Reader aircraft, Reader queues, Reader terminalsWithGates)
            throws MalformedSaveException, IOException {
        long tickloaded = loadTick(tick);
        List<Aircraft> aircraftLoaded = loadAircraft(aircraft);
        List<Terminal> terminalLoaded = loadTerminalsWithGates(terminalsWithGates, aircraftLoaded);
        TakeoffQueue takeoffLoaded = new TakeoffQueue();
        LandingQueue landingLoaded = new LandingQueue();
        TreeMap<Aircraft, Integer> loadingMapLoaded =
                new TreeMap<>(Comparator.comparing(Aircraft::getCallsign));
        loadQueues(queues, aircraftLoaded, takeoffLoaded, landingLoaded, loadingMapLoaded);
        ControlTower result =  new ControlTower(
                tickloaded, aircraftLoaded, landingLoaded, takeoffLoaded, loadingMapLoaded);
        for (Terminal terminal : terminalLoaded) {
            result.addTerminal(terminal);
        }
        return result;
    }
}