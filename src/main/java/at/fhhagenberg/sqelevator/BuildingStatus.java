package at.fhhagenberg.sqelevator;

import sqelevator.IElevator;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


// Mqtt -> RMI
public class BuildingStatus {

    private final MqttWrapper client;
    private IElevator elevatorController;
    private volatile boolean rmiConnected;
    private String rmiConnectionString;
    private ElevatorStatus[] elevators;
    private int elevatorNum;
    private int floorNum;
    private boolean upToDate;
    private static final String TOPIC_ELEVATOR_NUM = "NumberElevators/";
    private static final String TOPIC_FLOOR_NUM = "NumberFloors/";
    private boolean[] buttonPressedUp;
    private boolean[] buttonPressedDown;

    public BuildingStatus(MqttWrapper client, String rmiConnectionString) {
        this.rmiConnectionString = rmiConnectionString;
        if(rmiConnectionString.isEmpty()) {
            this.rmiConnectionString = "rmi://localhost/ElevatorSim";
        }

        this.client = client;

        connectRMI();
    }

    public void init() {
        // Send Initial State
        boolean messageSent = false;
        do{
            try {
                floorNum = elevatorController.getFloorNum();
                elevatorNum = elevatorController.getElevatorNum();

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

    private void rmiConnect() {
        //int errCount = 0;
        //int maxErr = 50;
        do{
            try {
                elevatorController = getRmiInterface(rmiConnectionString);
                rmiConnected = true;
                upToDate = false;
            } catch (RemoteException | MalformedURLException | NotBoundException e) {
                //errCount++;
                //if(errCount == maxErr){
                //    rmiConnected = false;
                //    break;
                //}
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
                boolean newButtonDown = elevatorController.getFloorButtonUp(i);
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
            for(int i = 0; i < elevatorNum; i++)
            {
                elevators[i].checkStatus(upToDate);
            }
            upToDate = true;
        } catch (RemoteException e) {
            connectRMI();
        }
    }
}
