package at.fhhagenberg.sqelevator;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import sqelevator.IElevator;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.eclipse.paho.mqttv5.common.MqttException;

// MQTT to Algo
public class ElevatorMain implements MqttCallback {
    private static final Logger LOGGER = Logger.getLogger(ElevatorMain.class.getName());
    private MqttWrapper mqttWrapper;
    private volatile boolean isNumberOfElevatorsInitialised = false;
    private volatile boolean isNumberOfFloorsInitialised = false;
    private static final String CONTROLLER_TOPIC_MAIN = "ElevatorControllerMain/";
    private static final String CONTROLLER_TOPIC_RMI = "ElevatorControllerRMI/";
    private static final String TOPIC_ELEVATOR_NUM = "NumberElevators/";
    private static final String TOPIC_FLOOR_NUM = "NumberFloors/";
    private int numberOfFloors;
    private int numberOfElevators;
    private ElevatorState[] state;

    private enum ElevatorState{
        UP,
        DOWN,
        UNCOMMITTED
    }
    private BuildingStorage building;

    // Main
    public static void main(String[] args) {
        ElevatorMain elevatorMain = new ElevatorMain("", "");
        MqttAdapter rmiMqttAdapter = new MqttAdapter("","", "");

        rmiMqttAdapter.startRMIPolling();
        elevatorMain.runSim();
    }

    public ElevatorMain(String mqtt, String clientId) {
        mqttWrapper = getMQTTClient(mqtt, clientId);

        mqttWrapper.subscribe(CONTROLLER_TOPIC_RMI + "#");
    }

    protected MqttWrapper getMQTTClient(String mqttConnectionString, String clientId) {
        if(mqttConnectionString.isEmpty()) {
            mqttConnectionString = "tcp://localhost:1883";
        }
        if(clientId.isEmpty()){
            clientId = "building_controller_client";
        }

        mqttWrapper = new MqttWrapper(mqttConnectionString, clientId, CONTROLLER_TOPIC_MAIN, this);  //URI, ClientId, Persistence
        return mqttWrapper;
    }

    public void runElevatorBigBrain(int elevator){
        final int sleepTime = 10;

        while (true) {
            switch (state[elevator]) {
                case UP:
                    state[elevator] = moveElevatorUp(elevator, sleepTime);
                    waitForDoorsOpen(elevator, sleepTime);
                    break;
                case DOWN:
                    state[elevator] = moveElevatorDown(elevator, sleepTime);
                    waitForDoorsOpen(elevator, sleepTime);
                    break;
                case UNCOMMITTED:
                    waitForDoorsOpen(elevator, sleepTime);
                    state[elevator] = ElevatorState.UP;
                    break;
            }
        }
    }

    private ElevatorState moveElevatorUp(int elevator, int sleepTime) {
        ElevatorState tmpState = ElevatorState.UP;
        int nextFloor = getNextFloor(elevator,true);
        int currentFloor = building.getCurrentFloor(elevator);

        if(nextFloor < currentFloor) {
            tmpState = ElevatorState.DOWN;
        }
        moveToFloor(elevator, tmpState, nextFloor, sleepTime);
        return tmpState;
    }

    private ElevatorState moveElevatorDown(int elevator, int sleepTime) {
        ElevatorState tmpState = ElevatorState.DOWN;
        int nextFloor = getNextFloor(elevator,false);

        moveToFloor(elevator, tmpState, nextFloor, sleepTime);
        if(nextFloor == 0){
            tmpState = ElevatorState.UP;
        }
        return tmpState;
    }

    private int getNextFloor(int elevator, boolean dirUp) {
        int nextFloor = -1;
        boolean[] elevButton = building.getFloorButtonStatus(elevator);
        if(dirUp)
        {
            // check if anyone wants to go up
            for(int i = building.getCurrentFloor(elevator) + 1; i < numberOfFloors; i++)
            {
                if(elevButton[i]){
                    nextFloor = i;
                    break;
                }
            }

            // Check if any outside button of previous Floor pressed
            for(int i = building.getCurrentFloor(elevator) + 1; i < numberOfFloors; i++)
            {
                if(building.getFloorState(i,true))
                {
                    if(i < nextFloor)
                    {
                        nextFloor = i;
                    } else {
                        break;
                    }
                }
            }
            if(nextFloor == -1){
                return getNextFloor(elevator, false);
            }
        } else {
            // check if anyone wants to go up
            for(int i = building.getCurrentFloor(elevator) - 1; i >= 0; i--)
            {
                if(elevButton[i]){
                    nextFloor = i;
                    break;
                }
            }

            // Check if any outside button of previous Floor pressed down
            for(int i = building.getCurrentFloor(elevator) - 1; i >= 0; i--)
            {
                if(building.getFloorState(i,false))
                {
                    if(i < nextFloor)
                    {
                        nextFloor = i;
                    } else {
                        break;
                    }
                }
            }
            if(nextFloor == -1) {
                nextFloor = 0;
            }
        }
        return nextFloor;
    }

    private void moveToFloor(int elevator, ElevatorState state, int floor, int sleepTime) {

        mqttWrapper.publishMQTTMessage(elevator + "/CommittedDirection/" , state.toString());
        mqttWrapper.publishMQTTMessage(elevator + "/Target/" , Integer.toString(floor));

        while (building.getCurrentFloor(elevator) != floor) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                // Handle MqttMessages Based on Topics
                LOGGER.log(Level.WARNING, String.format("Thread interrupted: ~ %s", e));
            }
        }
    }

    private void waitForDoorsOpen(int elevator, int sleepTime) {
        while (building.getDoorStatus(elevator) != IElevator.ELEVATOR_DOORS_OPEN) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, String.format("Thread interrupted: ~ %s", e));
            }
        }
    }

    public void runSim() {

        // Wait for Init to finish
        while(!isNumberOfFloorsInitialised || !isNumberOfElevatorsInitialised);

        building = new BuildingStorage(numberOfFloors, numberOfElevators);
        state = new ElevatorState[numberOfElevators];
        Arrays.fill(state, ElevatorState.UNCOMMITTED);
        // Ack Init
        // LOGGER.log(Level.INFO, "SendInitDone");
        mqttWrapper.publishMQTTMessage("InitDone" , "");

        List<Thread> threads = new ArrayList<>();
        int threadNum = numberOfElevators;
        for (int i = 0; i < threadNum; i++) {
            int finalI = i;
            threads.add(new Thread(() -> runElevatorBigBrain(finalI)));
            threads.get(i).start();
        }
    }

    @Override
    public void disconnected(MqttDisconnectResponse var1) {
        // not needed
    }
    @Override
    public void mqttErrorOccurred(MqttException var1) {
        // not needed
    }
    @Override
    public void messageArrived(String var1, MqttMessage var2) throws Exception {
        //LOGGER.log(Level.INFO, String.format("received: %s ~ %s", var1, var2));

        // Init topics
        if(var1.equals(CONTROLLER_TOPIC_RMI + TOPIC_FLOOR_NUM)) {
            numberOfFloors = Integer.parseInt(var2.toString());
            isNumberOfFloorsInitialised = true;
            return;
        }
        if(var1.equals(CONTROLLER_TOPIC_RMI + TOPIC_ELEVATOR_NUM)) {
            numberOfElevators = Integer.parseInt(var2.toString());
            isNumberOfElevatorsInitialised = true;
            return;
        }

        // split into subtopics
        String[] topics = var1.split("/");

        // Topics with deeps 3
        if(topics.length < 3) {
            //LOGGER.log(Level.INFO, String.format("ignoring: %s ~ %s", var1, var2));
            return;
        }
        if(topics[1].equals("FloorButtonUp")) {
            building.setFloorState(Integer.parseInt(topics[2]), true, var2.toString().equals("true"));
            return;
        }
        if(topics[1].equals("FloorButtonDown")) {
            building.setFloorState(Integer.parseInt(topics[2]), false, var2.toString().equals("true"));
            return;
        }

        if(topics[2].equals("floorNum")) {
            building.setCurrentFloor(Integer.parseInt(topics[1]), Integer.parseInt(var2.toString()));
            return;
        }
        if(topics[2].equals("position")) {
            //building.setCurrentFloor(Integer.parseInt(topics[1]), Integer.parseInt(var2.toString()));
            return;
        }
        if(topics[2].equals("target")) {
            building.setTargetFloor(Integer.parseInt(topics[1]), Integer.parseInt(var2.toString()));
            return;
        }
        if(topics[2].equals("committed_direction")) {
            building.setCommittedDirection(Integer.parseInt(topics[1]), Integer.parseInt(var2.toString()));
            return;
        }
        if(topics[2].equals("door_status")) {
            int a = Integer.parseInt(var2.toString());

            building.setDoorStatus(Integer.parseInt(topics[1]) , a);
            return;
        }

        // Topics with deeps 4
        if(topics.length < 4) {
            //LOGGER.log(Level.INFO, String.format("ignoring: %s ~ %s", var1, var2));
            return;
        }
        if(topics[2].equals("FloorButton")) {
            building.setFloorButtonStatus(Integer.parseInt(topics[1]), Integer.parseInt(topics[3]), var2.toString().equals("true"));
            return;
        }

        // Handle MqttMessages Based on Topics
        LOGGER.log(Level.INFO, String.format("Topic not handled: ~ %s", var1));
    }
    @Override
    public void deliveryComplete(IMqttToken var1){
        // not needed
    }
    @Override
    public void connectComplete(boolean var1, String var2){
        // not needed
    }
    @Override
    public void authPacketArrived(int var1, MqttProperties var2){
        // not needed
    }
}
