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

    private boolean[] elevatorButtons;

    private final MqttWrapper client;

    private final IElevator elevatorController;

    public ElevatorStatus(MqttWrapper client, IElevator elevatorController, int elevatorNum) throws RemoteException {
        this.client = client;
        this.elevatorController = elevatorController;
        this.elevatorNum.set(elevatorNum);
        this.elevatorButtons = new boolean[elevatorController.getFloorNum()];
    }

    private void updateAndPublishIfChanged(String topic, AtomicInteger currentValue, int newValue, boolean upToDate) {
        if (newValue != currentValue.get() || !upToDate) {
            currentValue.set(newValue);
            client.publishMQTTMessage(elevatorNum + "/" + topic, Integer.toString(newValue));
        }
    }

    void _updateAndPublishIfChanged(String topic, AtomicInteger currentValue, int newValue, boolean upToDate) {
        updateAndPublishIfChanged(topic, currentValue, newValue, upToDate);
    }

    public void checkStatus(boolean upToDate) throws RemoteException {
        int elevator = elevatorNum.get();

        updateAndPublishIfChanged("floorNum", floorNum, elevatorController.getElevatorFloor(elevator), upToDate);
        updateAndPublishIfChanged("position", position, elevatorController.getElevatorPosition(elevator), upToDate);
        updateAndPublishIfChanged("target", target, elevatorController.getTarget(elevator), upToDate);
        updateAndPublishIfChanged("committed_direction", committedDirection, elevatorController.getCommittedDirection(elevator), upToDate);
        updateAndPublishIfChanged("door_status", doorStatus, elevatorController.getElevatorDoorStatus(elevator), upToDate);

        for(int i = 0; i < elevatorController.getFloorNum(); i++)
        {
            boolean newButtonState = elevatorController.getElevatorButton(elevator, i);
            if(newButtonState != elevatorButtons[i] || !upToDate)
            {
                elevatorButtons[i] = newButtonState;
                client.publishMQTTMessage(elevatorNum + "/FloorButton/" + i, Boolean.toString(elevatorButtons[i]));
            }
        }
    }
}
