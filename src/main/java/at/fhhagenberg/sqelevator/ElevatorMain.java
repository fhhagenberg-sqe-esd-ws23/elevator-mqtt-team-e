package at.fhhagenberg.sqelevator;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import sqelevator.IElevator;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Vector;
import org.eclipse.paho.mqttv5.common.MqttException;

// MQTT to Algo
public class ElevatorMain implements MqttCallback {
    private MqttWrapper mqttWrapper;
    private volatile boolean IsNumberOfElevatorsInitialised = false;
    private volatile boolean IsNumberOfFloorsInitialised = false;
    private final String controllerTopicMain = "ElevatorControllerMain/";
    private final String controllerTopicRMI = "ElevatorControllerRMI/";
    private final String topicElevatorNum = "NumberElevators/";
    private final String topicFloorNum = "NumberFloors/";
    private int numberOfFloors = 99;
    private int numberOfElevators = 20;
    private ElevatorState[] state = new ElevatorState[99];

    private enum ElevatorState{
        UP,
        DOWN,
        UNCOMMITTED
    }
    private BuildingStorage building;

    // Main
    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException, MqttException {
        ElevatorMain EvMain = new ElevatorMain("", "");
        MqttAdapter adapt = new MqttAdapter("","", "");

        adapt.startPollingElevatorState();
        EvMain.runSim();
    }

    public ElevatorMain(String mqtt, String clientId) {
        mqttWrapper = getMQTTClient(mqtt, clientId);

        Arrays.fill(state, ElevatorState.UNCOMMITTED);

        building = new BuildingStorage(numberOfFloors, numberOfElevators);

        mqttWrapper.subscribe(controllerTopicRMI + "#");
    }

    protected MqttWrapper getMQTTClient(String mqttConnectionString, String clientId) {
        if(mqttConnectionString.isEmpty()) {
            mqttConnectionString = "tcp://localhost:1883";
        }
        if(clientId.isEmpty()){
            clientId = "building_controller_client";
        }

        mqttWrapper = new MqttWrapper(mqttConnectionString, clientId, controllerTopicMain, this);  //URI, ClientId, Persistence
        return mqttWrapper;
    }

    public void runElevatorBigBrain(int elevator){
        final int sleepTime = 10;

        while (true) {
            switch (state[elevator]) {
                case UP:
                    state[elevator] = moveElevatorUp(elevator, sleepTime);
                    break;
                case DOWN:
                    state[elevator] = moveElevatorDown(elevator, sleepTime);
                    break;
                case UNCOMMITTED:
                    waitForDoorsOpen(elevator, sleepTime);
                    state[elevator] = ElevatorState.UP;
                    break;
            }
        }
    }

    private ElevatorState moveElevatorUp(int elevator, int sleepTime) {
        ElevatorState state = ElevatorState.UP;
        int nextFloor = GetNextFloor(elevator,true);
        if(nextFloor < building.getCurrentFloor(elevator)) {
            state = ElevatorState.DOWN;
        }
        moveToFloor(elevator, state, nextFloor, sleepTime);
        return state;
    }

    private ElevatorState moveElevatorDown(int elevator, int sleepTime) {
        ElevatorState state = ElevatorState.DOWN;
        int nextFloor = GetNextFloor(elevator,false);
        moveToFloor(elevator, state, nextFloor, sleepTime);
        if(nextFloor == 0){
            state = ElevatorState.UP;
        }
        return state;
    }

    private int GetNextFloor(int elevator, boolean dirUp)
    {
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
            for(int i = building.getCurrentFloor(elevator); i < numberOfFloors; i++)
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
                return GetNextFloor(elevator, false);
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
            for(int i = building.getCurrentFloor(elevator); i < numberOfFloors; i++)
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
            }
        }
        waitForDoorsOpen(elevator, sleepTime);
    }

    private void waitForDoorsOpen(int elevator, int sleepTime) {
        while (building.getDoorStatus(elevator) != IElevator.ELEVATOR_DOORS_OPEN) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
            }
        }
    }

    private void AddTopics(int elevatorNum, int floorNum)
    {
        // TODO Add all remaining Topics
        // These can be added in a for loop generated from elevatorNum and floorNum
        // #useless
    }

    public void runSim(){
        // Wait for Init to finish

        while(!IsNumberOfFloorsInitialised || !IsNumberOfElevatorsInitialised){}

        //building = new BuildingStorage(numberOfFloors, numberOfElevators);

        AddTopics(numberOfElevators, numberOfFloors);

        Vector<Thread> threads = new Vector<>();
        int threadNum = numberOfElevators;
        for(int i = 0; i < threadNum; i++)
        {
            int finalI = i;
            threads.add(new Thread(() -> runElevatorBigBrain(finalI)));
            threads.get(i).start();
        }
    }



    @Override
    public void disconnected(MqttDisconnectResponse var1){
    }
    @Override
    public void mqttErrorOccurred(MqttException var1){}
    @Override
    public void messageArrived(String var1, MqttMessage var2) throws Exception {
        //System.out.println("received: " + var1 + " ~ " + var2);

        // Init topics
        if(var1.equals(controllerTopicRMI + topicFloorNum)) {
            numberOfFloors = Integer.parseInt(var2.toString());
            IsNumberOfFloorsInitialised = true;
            return;
        }
        if(var1.equals(controllerTopicRMI + topicElevatorNum)) {
            numberOfElevators = Integer.parseInt(var2.toString());
            IsNumberOfElevatorsInitialised = true;
            return;
        }
        if(!IsNumberOfFloorsInitialised || !IsNumberOfElevatorsInitialised || building == null){
            return;
        }

        // split into subtopics
        String[] topics = var1.split("/");

        // Topics with deeps 3
        if(topics.length < 3) {
            System.out.println("ignoring: " + var1 + " ~ " + var2);
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
            System.out.println("ignoring: " + var1 + " ~ " + var2);
            return;
        }
        if(topics[2].equals("FloorButton")) {
            building.setFloorButtonStatus(Integer.parseInt(topics[1]), Integer.parseInt(topics[3]), var2.toString().equals("true"));
            return;
        }

        // Handle MqttMessages Based on Topics
        System.out.println("Topic not handeled ~ " + var1);
    }
    @Override
    public void deliveryComplete(IMqttToken var1){}
    @Override
    public void connectComplete(boolean var1, String var2){}
    @Override
    public void authPacketArrived(int var1, MqttProperties var2){}
}
