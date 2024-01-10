package at.fhhagenberg.sqelevator;
import sqelevator.IElevator;
import java.rmi.Naming;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.paho.mqttv5.common.MqttException;

public class ElevatorMain {
    private volatile boolean rmiConnected = false;
    private String rmiConnectionString;
    private String mqttConnectionString;
    private MqttWrapper mqttWrapper;
    private final ExecutorService executorService;
    private final int pollingInterval = 250;
    private IElevator elevatorController;

    public ElevatorMain(String rmi, String mqtt) {
        this.rmiConnectionString = rmi;
        if(this.rmiConnectionString.isEmpty()) {
            this.rmiConnectionString = "rmi://localhost/ElevatorSim";
        }

        this.mqttConnectionString = mqtt;
        if(this.mqttConnectionString.isEmpty()) {
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
                elevators[i] = new ElevatorStatus(mqttWrapper, elevatorController, i);
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

    protected MqttWrapper getMQTTClient() throws MqttException {
        mqttWrapper = new MqttWrapper(this.mqttConnectionString, "building_controller_client");  //URI, ClientId, Persistence
        return mqttWrapper;
    }

    public void connect(){
        try {
            rmiConnected = false;
            elevatorController = getRmiInterface();
            rmiConnected = true;
            mqttWrapper = getMQTTClient();
            mqttWrapper.publishMQTTMessage("ElevatorController", "RMI Connection established.");
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

    public void runElevatorBigBrain(int elevator){
        final int sleepTime = 10;
        while(true){
            while (!rmiConnected) Thread.onSpinWait();

            // Check the weight of the elevator to ensure it's within capacity
            try {
                /*
                if (elevatorController.getElevatorWeight(elevator) > MAX_CAPACITY) {
                    // Handle overweight situation (e.g., do not move, alert, etc.)
                    continue;
                }
                 */

                // Check for call signals on each floor and passenger requests inside the elevator
                boolean shouldMove = false;
                int targetFloor = -1;

                for (int floor = 0; floor < elevatorController.getFloorNum(); floor++) {
                    if (elevatorController.getFloorButtonUp(floor) || elevatorController.getFloorButtonDown(floor)) {
                        // If a call signal is detected, set targetFloor to this floor
                        targetFloor = floor;
                        shouldMove = true;
                        break;
                    }
                }

                // Check inside the elevator for passenger requests
                for (int floor = 0; floor < elevatorController.getFloorNum(); floor++) {
                    if (elevatorController.getElevatorButton(elevator, floor)) {
                        // If a passenger has selected a floor, set targetFloor to this floor
                        targetFloor = floor;
                        shouldMove = true;
                        break;
                    }
                }

                if (shouldMove) {
                    // Move the elevator towards the target floor
                    elevatorController.setTarget(elevator, targetFloor);

                    // Wait until closest floor is the target floor and speed is back to zero
                    while (elevatorController.getElevatorFloor(elevator) != targetFloor ||
                            elevatorController.getElevatorSpeed(elevator) > 0) {
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
            } catch (RemoteException e) {
                throw new RuntimeException(e);
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
                    runElevatorBigBrain(finalI);
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
