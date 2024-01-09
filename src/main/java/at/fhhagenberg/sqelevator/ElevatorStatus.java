package at.fhhagenberg.sqelevator;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import sqelevator.IElevator;

import java.rmi.RemoteException;


public class ElevatorStatus {
    int elevatorNum = 0;
    int floorNum = 0;
    int position = 0;
    int target = 0;
    int committed_direction = 0;
    int door_status = 0;
    int speed = 0;
    int acceleration = 0;
    int capacity = 0;
    int weight = 0;

    private MqttClient client;
    private IElevator elevatorController;

    public ElevatorStatus(MqttClient client, IElevator elevatorController, int elevatorNum){
        this.client = client;
        this.elevatorController = elevatorController;
        this.elevatorNum = elevatorNum;
    }
    private void publishMQTTMessage(String topic, String message) {
        try {
            client.publish(topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            System.err.println("Error publishing MQTT message: " + e.getMessage());
        }
    }
    void checkStatus()
    {
        try {
            if(floorNum != elevatorController.getElevatorFloor(elevatorNum))
            {
                floorNum = elevatorController.getElevatorFloor(elevatorNum);
                publishMQTTMessage("ElevatorController/"+elevatorNum+"/floorNum", Integer.toString(floorNum));
            }
            if(position != elevatorController.getElevatorPosition(elevatorNum))
            {
                position = elevatorController.getElevatorPosition(elevatorNum);
                publishMQTTMessage("ElevatorController/"+elevatorNum+"/position", Integer.toString(position));
            }
            if(target != elevatorController.getTarget(elevatorNum))
            {
                target = elevatorController.getTarget(elevatorNum);
                publishMQTTMessage("ElevatorController/"+elevatorNum+"/target", Integer.toString(target));
            }
            if(committed_direction != elevatorController.getCommittedDirection(elevatorNum))
            {
                committed_direction = elevatorController.getCommittedDirection(elevatorNum);
                publishMQTTMessage("ElevatorController/"+elevatorNum+"/committed_direction", Integer.toString(committed_direction));
            }
            if(door_status != elevatorController.getElevatorDoorStatus(elevatorNum))
            {
                door_status = elevatorController.getElevatorDoorStatus(elevatorNum);
                publishMQTTMessage("ElevatorController/"+elevatorNum+"/door_status", Integer.toString(door_status));
            }
            if(speed != elevatorController.getElevatorSpeed(elevatorNum))
            {
                speed = elevatorController.getElevatorSpeed(elevatorNum);
                publishMQTTMessage("ElevatorController/"+elevatorNum+"/speed", Integer.toString(speed));
            }
            if(acceleration != elevatorController.getElevatorAccel(elevatorNum))
            {
                acceleration = elevatorController.getElevatorAccel(elevatorNum);
                publishMQTTMessage("ElevatorController/"+elevatorNum+"/acceleration", Integer.toString(acceleration));
            }
            if(capacity != elevatorController.getElevatorCapacity(elevatorNum))
            {
                capacity = elevatorController.getElevatorCapacity(elevatorNum);
                publishMQTTMessage("ElevatorController/"+elevatorNum+"/capacity", Integer.toString(capacity));
            }
            if(weight != elevatorController.getElevatorWeight(elevatorNum))
            {
                weight = elevatorController.getElevatorWeight(elevatorNum);
                publishMQTTMessage("ElevatorController/"+elevatorNum+"/weight", Integer.toString(weight));
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
