package at.fhhagenberg.sqelevator;

import java.util.Arrays;

// IElevator Datamodel
public class Elevator {
    private final int numberOfFloors;
    private int elevatorNumber;
    private int currentFloor;
    private int targetFloor;
    private int committedDirection;
    private int doorStatus;
    private int elevatorSpeed;
    private int elevatorAccel;
    private boolean[] floorButtonStatus; // Status of floor buttons inside the elevator
    private boolean[] servicedFloors; // Floors serviced by this elevator
    private int elevatorCapacity;
    private int elevatorWeight;

    // Constructor
    public Elevator(int elevatorNumber, int numberOfFloors, int elevatorCapacity) {
        this.numberOfFloors = numberOfFloors;
        this.elevatorNumber = elevatorNumber;
        this.currentFloor = 0;
        this.targetFloor = 0;
        this.committedDirection = IElevator.ELEVATOR_DIRECTION_UNCOMMITTED;
        this.doorStatus = IElevator.ELEVATOR_DOORS_CLOSED;
        this.elevatorSpeed = 0;
        this.elevatorAccel = 0;
        this.floorButtonStatus = new boolean[numberOfFloors];
        Arrays.fill(this.floorButtonStatus, true);
        this.servicedFloors = new boolean[numberOfFloors];
        Arrays.fill(this.servicedFloors, true);
        this.elevatorCapacity = elevatorCapacity;
        this.elevatorWeight = 0;
    }

    // Getters and Setters
    public int getElevatorNumber() {
        return elevatorNumber;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public void setTargetFloor(int targetFloor) {
        this.targetFloor = targetFloor;
    }

    public int getCommittedDirection() {
        return committedDirection;
    }

    public void setCommittedDirection(int committedDirection) {
        this.committedDirection = committedDirection;
    }

    public int getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(int doorStatus) {
        this.doorStatus = doorStatus;
    }

    public int getElevatorSpeed() {
        return elevatorSpeed;
    }

    public void setElevatorSpeed(int elevatorSpeed) {
        this.elevatorSpeed = elevatorSpeed;
    }

    public int getElevatorAccel() {
        return elevatorAccel;
    }

    public void setElevatorAccel(int elevatorAccel) {
        this.elevatorAccel = elevatorAccel;
    }

    public boolean getFloorButtonStatus(int floor) {
        if(floor < this.numberOfFloors) {
            return floorButtonStatus[floor];
        }
        return false;
    }

    public void setFloorButtonStatus(boolean[] floorButtonStatus) {
        this.floorButtonStatus = floorButtonStatus;
    }

    public boolean getServicedFloors(int floor) {
        if(floor < this.numberOfFloors) {
            return servicedFloors[floor];
        }
        return false;
    }

    public void setServicesFloor(int floor, boolean service) {
        if(floor < this.numberOfFloors) {
            this.servicedFloors[floor] = service;
        }
    }

    public int getElevatorCapacity() {
        return elevatorCapacity;
    }

    public int getElevatorWeight() {
        return elevatorWeight;
    }

    public void setElevatorWeight(int elevatorWeight) {
        this.elevatorWeight = elevatorWeight;
    }
}

