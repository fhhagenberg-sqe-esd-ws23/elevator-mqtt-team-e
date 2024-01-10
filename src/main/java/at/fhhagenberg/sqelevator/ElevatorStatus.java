package at.fhhagenberg.sqelevator;
import sqelevator.IElevator;

import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;


public class ElevatorStatus {
    AtomicInteger elevatorNum = new AtomicInteger(0);
    AtomicInteger floorNum = new AtomicInteger(0);
    AtomicInteger position = new AtomicInteger(0);
    AtomicInteger target = new AtomicInteger(0);
    AtomicInteger committedDirection = new AtomicInteger(0);
    AtomicInteger doorStatus = new AtomicInteger(0);
    AtomicInteger speed = new AtomicInteger(0);
    AtomicInteger acceleration = new AtomicInteger(0);
    AtomicInteger capacity = new AtomicInteger(0);
    AtomicInteger weight = new AtomicInteger(0);

    private MqttWrapper client;

    private IElevator elevatorController;

    public ElevatorStatus(MqttWrapper client, IElevator elevatorController, int elevatorNum){
        this.client = client;
        this.elevatorController = elevatorController;
        this.elevatorNum.set(elevatorNum);
    }


    private void updateAndPublishIfChanged(String topic, AtomicInteger currentValue, int newValue) {
        if (newValue != currentValue.get()) {
            currentValue.set(newValue);
            client.publishMQTTMessage("ElevatorController/" + elevatorNum + "/" + topic, Integer.toString(newValue));
        }
    }

    void checkStatus()
    {
        try {
            updateAndPublishIfChanged("floorNum", floorNum, elevatorController.getElevatorFloor(elevatorNum.get()));
            updateAndPublishIfChanged("position", position, elevatorController.getElevatorPosition(elevatorNum.get()));
            updateAndPublishIfChanged("target", target, elevatorController.getTarget(elevatorNum.get()));
            updateAndPublishIfChanged("committed_direction", committedDirection, elevatorController.getCommittedDirection(elevatorNum.get()));
            updateAndPublishIfChanged("door_status", doorStatus, elevatorController.getElevatorDoorStatus(elevatorNum.get()));
            updateAndPublishIfChanged("speed", speed, elevatorController.getElevatorSpeed(elevatorNum.get()));
            updateAndPublishIfChanged("acceleration", acceleration, elevatorController.getElevatorAccel(elevatorNum.get()));
            updateAndPublishIfChanged("capacity", capacity, elevatorController.getElevatorCapacity(elevatorNum.get()));
            updateAndPublishIfChanged("weight", weight, elevatorController.getElevatorWeight(elevatorNum.get()));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
