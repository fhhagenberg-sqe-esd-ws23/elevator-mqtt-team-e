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

    private final static String topicElevatorNum = "NumberElevators/";
    private final static  String topicFloorNum = "NumberFloors/";



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

    public void init() throws RemoteException {
        // Send Initial State
        boolean messageSent = false;
        int numFloors = 0;
        do{
            try {
                numFloors = elevatorController.getFloorNum();
                elevatorNum = elevatorController.getElevatorNum();

                client.publishRetainedMQTTMessage(topicElevatorNum, Integer.toString(elevatorNum));
                client.publishRetainedMQTTMessage(topicFloorNum, Integer.toString(numFloors));

                messageSent = true;

                buttonPressedUp = new boolean[numFloors];
                buttonPressedDown = new boolean[numFloors];
                for(int i = 0; i < numFloors; i++){
                    buttonPressedUp[i] = elevatorController.getFloorButtonUp(i);
                    buttonPressedDown[i] = elevatorController.getFloorButtonDown(i);

                    // Send Message
                    client.publishMQTTMessage("FloorButtonUp/" + i, Boolean.toString(buttonPressedUp[i]));
                    client.publishMQTTMessage("FloorButtonDown/" + i, Boolean.toString(buttonPressedDown[i]));
                }


                elevators = new ElevatorStatus[elevatorNum];
                for(int i = 0; i < elevatorNum; i++){
                    elevators[i] = new ElevatorStatus(client, elevatorController, i);
                }

            } catch (RemoteException e) {
            connectRMI();
            }
        }while(!messageSent);

    }

    public void connectRMI(){
        rmiConnected = false;
        rmiConnect();
    }

    private void rmiConnect()
    {
        int errCount = 0;
        int maxErr = 50;
        do{
            try {
                elevatorController = getRmiInterface(rmiConnectionString);
                rmiConnected = true;
            } catch (RemoteException | MalformedURLException | NotBoundException e) {
                errCount++;
                if(errCount == maxErr){
                    rmiConnected = false;
                    break;
                }
            }
        }while(!rmiConnected);
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

    public void sendStatus(){
        try {
            // Check FloorButtons
            for(int i = 0; i < elevatorController.getFloorNum(); i++)
            {
                boolean newButtonUp = elevatorController.getFloorButtonUp(i);
                boolean newButtonDown = elevatorController.getFloorButtonUp(i);
                if(newButtonUp != buttonPressedUp[i]){
                    buttonPressedUp[i] = newButtonUp;
                    client.publishMQTTMessage("FloorButtonUp/" + i + "/", Boolean.toString(buttonPressedUp[i]));
                }

                if(newButtonDown != buttonPressedDown[i]){
                    buttonPressedDown[i] = newButtonDown;
                    client.publishMQTTMessage("FloorButtonDown/" + i + "/", Boolean.toString(buttonPressedDown[i]));
                }
            }
            // Check Elevator things
            for(int i = 0; i < elevatorNum; i++)
            {
                elevators[i].checkStatus();
            }
        } catch (RemoteException e) {
            connectRMI();
        }
    }
}
