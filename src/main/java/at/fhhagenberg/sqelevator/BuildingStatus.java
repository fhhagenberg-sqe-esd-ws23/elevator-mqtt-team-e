package at.fhhagenberg.sqelevator;

import sqelevator.IElevator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


// Mqtt -> RMI
public class BuildingStatus {

    private final MqttWrapper client;
    public MqttWrapper getMqttClient(){
        return this.client;
    }
    private IElevator elevatorController;
    public void setElevatorController(IElevator elevator){
        this.elevatorController = elevator;
    }
    public IElevator getElevatorController(){
        return this.elevatorController;
    }
    public volatile boolean rmiConnected;
    private String rmiConnectionString;
    public String getRmiConnectionString(){
        return this.rmiConnectionString;
    }
    private ElevatorStatus[] elevators;
    public void setElevators(ElevatorStatus[] elevators) {
        this.elevators = elevators;
    }
    public ElevatorStatus[] getElevators() {
        return elevators;
    }

    private boolean upToDate;
    public void setUpToDate(boolean upToDate){
        this.upToDate = upToDate;
    }
    private static final String TOPIC_ELEVATOR_NUM = "NumberElevators/";
    private static final String TOPIC_FLOOR_NUM = "NumberFloors/";
    private boolean[] buttonPressedUp;
    public boolean[] getButtonPressedUp(){
        return this.buttonPressedUp;
    }
    private boolean[] buttonPressedDown;
    public boolean[] getButtonPressedDown(){
        return this.buttonPressedDown;
    }

    public BuildingStatus(MqttWrapper client, String rmiConnectionString) {
        this.rmiConnectionString = rmiConnectionString;
        this.client = client;
    }

    public void init() {
        // Send Initial State
        boolean messageSent = false;
        do{
            try {
                int floorNum = elevatorController.getFloorNum();
                int elevatorNum = elevatorController.getElevatorNum();

                client.publishRetainedMQTTMessage(TOPIC_ELEVATOR_NUM, Integer.toString(elevatorNum));
                client.publishRetainedMQTTMessage(TOPIC_FLOOR_NUM, Integer.toString(floorNum));
                // Retained Msg was sent
                messageSent = true;

                buttonPressedUp = new boolean[floorNum];
                buttonPressedDown = new boolean[floorNum];
                for(int i = 0; i < floorNum; i++){
                    buttonPressedUp[i] = elevatorController.getFloorButtonUp(i);
                    buttonPressedDown[i] = elevatorController.getFloorButtonDown(i);

                }
                elevators = new ElevatorStatus[elevatorNum];
                for(int i = 0; i < elevatorNum; i++){
                    elevators[i] = new ElevatorStatus(client, elevatorController, i);
                }

                upToDate = false;

            } catch (RemoteException e) {
                connectRMI();
            }
        }while(!messageSent);
    }

    public void connectRMI() {
        rmiConnected = false;
        rmiConnect();
    }

    public void rmiConnect() {

        do{
            try {
                elevatorController = getRmiInterface(rmiConnectionString);
                rmiConnected = true;
                upToDate = false;
            } catch (RemoteException | MalformedURLException | NotBoundException e) {
                rmiConnected = false;
            }
        } while(!rmiConnected);
    }

    protected IElevator getRmiInterface(String rmiConnectionString) throws MalformedURLException, RemoteException, NotBoundException {
        return (IElevator) Naming.lookup(rmiConnectionString);
    }

    public void reportTargetToRMI(int elNum, int target) throws RemoteException {
        elevatorController.setTarget(elNum, target);
    }

    public void reportCommitedDirectionToRMI(int elNum, int dir) throws RemoteException {
        elevatorController.setCommittedDirection(elNum, dir);
    }

    public void sendStatus() {
        try {
            // Check FloorButtons
            for(int i = 0; i < elevatorController.getFloorNum(); i++)
            {
                boolean newButtonUp = elevatorController.getFloorButtonUp(i);
                boolean newButtonDown = elevatorController.getFloorButtonDown(i);
                if(newButtonUp != buttonPressedUp[i] || !upToDate){
                    buttonPressedUp[i] = newButtonUp;
                    client.publishMQTTMessage("FloorButtonUp/" + i + "/", Boolean.toString(buttonPressedUp[i]));
                }

                if(newButtonDown != buttonPressedDown[i] || !upToDate){
                    buttonPressedDown[i] = newButtonDown;
                    client.publishMQTTMessage("FloorButtonDown/" + i + "/", Boolean.toString(buttonPressedDown[i]));
                }
            }
            // Check Elevator things
            for(int i = 0; i < elevatorController.getElevatorNum(); i++)
            {
                elevators[i].checkStatus(upToDate);
            }
            upToDate = true;
        } catch (RemoteException e) {
            connectRMI();
        }
    }
}
