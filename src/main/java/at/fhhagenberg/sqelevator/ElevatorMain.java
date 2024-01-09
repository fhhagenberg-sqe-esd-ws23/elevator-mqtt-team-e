package at.fhhagenberg.sqelevator;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import sqelevator.IElevator;
import java.rmi.Naming;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;

public class ElevatorMain {
    private boolean rmiConnected = false;
    private String rmiConnectionString;
    private String mqttConnectionString;
    private MqttClient client;

    private IElevator elevatorController;
    public ElevatorMain(String rmi, String mqtt) {
        this.rmiConnectionString = rmi;
        if(this.rmiConnectionString == "") {
            this.rmiConnectionString = "rmi://localhost/ElevatorSim";
        }

        this.mqttConnectionString = mqtt;
        if(this.mqttConnectionString == "") {
            this.mqttConnectionString = "tcp://localhost:1883";
        }

        connect();
    }

    // Overwrite for Mocktesting
    protected IElevator getRmiInterface() throws MalformedURLException, RemoteException, NotBoundException {
        return (IElevator)Naming.lookup(this.rmiConnectionString);
    }



    protected MqttClient getMQTTClient() throws MqttException {
        MqttClient client = new MqttClient(this.mqttConnectionString, "building_controller_client", new MemoryPersistence());  //URI, ClientId, Persistence
        client.connect();

        return client;
    }

    private void publishMQTTMessage(String topic, String message) {
        try {
            client.publish(topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            System.err.println("Error publishing MQTT message: " + e.getMessage());
        }
    }

    public void connect(){
        try {
            rmiConnected = false;
            elevatorController = getRmiInterface();
            rmiConnected = true;
            client = getMQTTClient();
            publishMQTTMessage("test/topic", "Pascal is a huan");

        }
        catch (NotBoundException e) {
            System.err.println("Remote server not reachable. " + e.getMessage());
        }
        catch (MalformedURLException e) {
            System.err.println("Invalid URL: " + e.getMessage());
        }
        catch (RemoteException e) {
            System.err.println("Remote exception on connecting: " + e.getMessage());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public void runSim() throws RemoteException{

        final int elevator = 0;
        final int numberOfFloors = elevatorController.getFloorNum();
        final int sleepTime = 60;

        // First: Starting from ground floor, go up to the top floor, stopping in each floor

        // Set the committed direction displayed on the elevator to up
        elevatorController.setCommittedDirection(elevator, IElevator.ELEVATOR_DIRECTION_UP);

        for (int nextFloor=1; nextFloor<numberOfFloors; nextFloor++) {

            // Set the target floor to the next floor above
            elevatorController.setTarget(elevator, nextFloor);

            // Wait until closest floor is the target floor and speed is back to zero
            while (elevatorController.getElevatorFloor(elevator) < nextFloor || elevatorController.getElevatorSpeed(elevator) > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {}
            }

            // Wait until doors are open before setting the next direction
            while (elevatorController.getElevatorDoorStatus(elevator) != IElevator.ELEVATOR_DOORS_OPEN) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {}
            }
        }

        // Second, go back from the top floor to the ground floor in one move

        // Set the committed direction displayed on the elevator to down
        elevatorController.setCommittedDirection(elevator, IElevator.ELEVATOR_DIRECTION_DOWN);

        // Set the target floor to the ground floor (floor number 0)
        elevatorController.setTarget(elevator, 0);

        // Wait until ground floor has been reached
        while (elevatorController.getElevatorFloor(elevator) > 0 || elevatorController.getElevatorSpeed(elevator) > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {}
        }

        // Set the committed direction to uncommitted when back at the ground floor
        elevatorController.setCommittedDirection(elevator, IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);



    }

    public static void main(String[] args) throws RemoteException {
        ElevatorMain EvMain = new ElevatorMain("","");
        EvMain.runSim();
    }
}
