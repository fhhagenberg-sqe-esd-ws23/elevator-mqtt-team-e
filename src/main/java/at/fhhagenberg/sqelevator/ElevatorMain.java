package at.fhhagenberg.sqelevator;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import sqelevator.IElevator;
import java.rmi.Naming;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;

public class ElevatorMain {
    private boolean rmiConnected = false;
    private String rmiConnectionString;
    private String mqttConnectionString;
    private MqttClient client;
    private ExecutorService executorService;
    private final int pollingInterval = 250;
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

        this.executorService = Executors.newSingleThreadExecutor();
        startPollingElevatorState();
    }

    // Overwrite for Mocktesting
    protected IElevator getRmiInterface() throws MalformedURLException, RemoteException, NotBoundException {
        return (IElevator)Naming.lookup(this.rmiConnectionString);
    }



    private void startPollingElevatorState() {
        executorService.submit(() -> {

            int elevatorNum = 0;
            try {
                elevatorNum = elevatorController.getElevatorNum();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            ElevatorStatus[] elevators = new ElevatorStatus[elevatorNum];
            for(int i = 0; i < elevatorNum; i++)
            {
                elevators[i] = new ElevatorStatus(client, elevatorController, i);
            }

            while (true) {
                for(int i = 0; i < elevatorNum; i++)
                {
                    elevators[i].checkStatus();
                }

                try {
                    Thread.sleep(pollingInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
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
            publishMQTTMessage("ElevatorController", "RMI Connection established.");

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

    public void runElevevatorBigBrain(int elevator) throws RemoteException {

        final int sleepTime = 10;
        while(true){
            // Set the committed direction displayed on the elevator to up
            elevatorController.setCommittedDirection(elevator, IElevator.ELEVATOR_DIRECTION_UP);

            for (int nextFloor = 1; nextFloor < elevatorController.getFloorNum(); nextFloor++) {

                // Set the target floor to the next floor above
                elevatorController.setTarget(elevator, nextFloor);

                // Wait until closest floor is the target floor and speed is back to zero
                while (elevatorController.getElevatorFloor(elevator) < nextFloor || elevatorController.getElevatorSpeed(elevator) > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                }

                // Wait until doors are open before setting the next direction
                while (elevatorController.getElevatorDoorStatus(elevator) != IElevator.ELEVATOR_DOORS_OPEN) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                }
            }

            // Second, go back from the top floor to the ground floor in one move
            // Set the committed direction displayed on the elevator to down
            elevatorController.setCommittedDirection(elevator, IElevator.ELEVATOR_DIRECTION_DOWN);

            for (int nextFloor = elevatorController.getFloorNum() - 1; nextFloor >= 0; nextFloor--) {

                // Setzen Sie das Zielgeschoss auf das nächste darunterliegende Stockwerk
                elevatorController.setTarget(elevator, nextFloor);

                // Warten Sie, bis das nächstliegende Stockwerk das Zielgeschoss ist und die Geschwindigkeit wieder null ist
                while (elevatorController.getElevatorFloor(elevator) > nextFloor || elevatorController.getElevatorSpeed(elevator) > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {}
                }

                // Warten Sie, bis die Türen geöffnet sind, bevor Sie die nächste Richtung einstellen
                while (elevatorController.getElevatorDoorStatus(elevator) != IElevator.ELEVATOR_DOORS_OPEN) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {}
                }
            }

            // Set the committed direction to uncommitted when back at the ground floor
            elevatorController.setCommittedDirection(elevator, IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);

            // Wait until doors are open before setting the next direction
            while (elevatorController.getElevatorDoorStatus(elevator) != IElevator.ELEVATOR_DOORS_OPEN) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void runSim() throws RemoteException{
        Vector<Thread> threads = new Vector<Thread>();
        int threadNum = elevatorController.getElevatorNum();
        for(int i = 0; i < threadNum; i++)
        {
            int finalI = i;
            threads.add(new Thread(new Runnable() {
                public void run() {
                    try {
                        runElevevatorBigBrain(finalI);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }));
            threads.get(i).start();
        }
    }

    public static void main(String[] args) throws RemoteException {
        ElevatorMain EvMain = new ElevatorMain("","");
        EvMain.runSim();
    }
}
