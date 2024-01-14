package at.fhhagenberg.sqelevator;
import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import sqelevator.IElevator;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Vector;
import org.eclipse.paho.mqttv5.common.MqttException;

public class ElevatorMain implements IMqttMessageListener{
    private MqttWrapper mqttWrapper;

    private volatile boolean IsNumberOfElevatorsInitialised = false;
    private volatile boolean IsNumberOfFloorsInitialised = false;
    private final String topicElevatorNum = "ElevatorController/NumberFloors/";
    private final String topicFloorNum = "ElevatorController/NumberElevators/";
    private int numberOfFloors = 0;
    private int numberOfElevators = 0;
    private ElevatorState state = ElevatorState.UNCOMMITTED;

    private enum ElevatorState{
        UP,
        DOWN,
        UNCOMMITTED
    }
    private BuildingStorage building;
    public ElevatorMain(String mqtt, String clientId) {
        mqttWrapper = getMQTTClient(mqtt, clientId);

        mqttWrapper.subscribe(topicElevatorNum);
        mqttWrapper.subscribe(topicFloorNum);
    }

    protected MqttWrapper getMQTTClient(String mqttConnectionString, String clientId) {
        if(mqttConnectionString.isEmpty()) {
            mqttConnectionString = "tcp://localhost:1883";
        }
        if(clientId.isEmpty()){
            clientId = "building_controller_client";
        }

        mqttWrapper = new MqttWrapper(mqttConnectionString, clientId);  //URI, ClientId, Persistence
        return mqttWrapper;
    }

    public void runElevatorBigBrain(int elevator){
        final int sleepTime = 10;
        int numOfFloors = building.getFloorNum();

        while (true) {
            switch (state) {
                case UP:
                    state = moveElevatorUp(elevator, sleepTime);
                    break;
                case DOWN:
                    state = moveElevatorDown(elevator, sleepTime);
                    break;
                case UNCOMMITTED:
                    waitForDoorsOpen(elevator, sleepTime);
                    state = ElevatorState.UP;
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
                    }else{
                        break;
                    }
                }
            }
            if(nextFloor == -1){
                return GetNextFloor(elevator, false);
            }
        }else{
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
                    }else{
                        break;
                    }
                }
            }
            if(nextFloor == -1){
                nextFloor = 0;
            }
        }
        return nextFloor;
    }

    private void moveToFloor(int elevator, ElevatorState state, int floor, int sleepTime) {
        // TODO mqtt setCommittedDirection(state)
        // TODO mqtt setTarget(elevator, floor)
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
    }

    public void runSim(){
        // Wait for Init to finish
        while(!IsNumberOfFloorsInitialised || !IsNumberOfElevatorsInitialised){}

        building = new BuildingStorage(numberOfElevators, numberOfFloors);
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

    public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException, MqttException {
        ElevatorMain EvMain = new ElevatorMain("", "");
        MqttAdapter adapt = new MqttAdapter("","", "");

        adapt.startPollingElevatorState();
        EvMain.runSim();
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        if(s.equals(topicFloorNum)){
            numberOfFloors = Integer.parseInt(Arrays.toString(mqttMessage.getPayload()));
            IsNumberOfFloorsInitialised = true;
        }
        if(s.equals(topicElevatorNum)){
            numberOfElevators = Integer.parseInt(Arrays.toString(mqttMessage.getPayload()));
            IsNumberOfElevatorsInitialised = true;
        }
        if(!IsNumberOfFloorsInitialised || !IsNumberOfElevatorsInitialised){
            return;
        }

        // Handle MqttMessages Based on Topics

    }
}
