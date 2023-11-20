package at.fhhagenberg.sqelevator;

import java.util.Arrays;

/**
 * Represents an elevator in the building.
 */
public class Elevator {
    /** The total number of floors in the building where the elevator operates.*/
    private final int numberOfFloors;

    /** The unique number identifying this elevator.*/
    private int elevatorNumber;

    /** The current floor where the elevator is positioned.*/
    private int currentFloor;

    /** The target floor towards which the elevator is moving.*/
    private int targetFloor;

    /** The direction in which the elevator is committed to moving.*/
    private int committedDirection;

    /** The status of the elevator doors (open, closed, etc.).*/
    private int doorStatus;

    /** The speed of the elevator's movement.*/
    private int elevatorSpeed;

    /** The acceleration of the elevator's movement.*/
    private int elevatorAccel;

    /** The status of floor buttons inside the elevator (pressed or not).*/
    private boolean[] floorButtonStatus;

    /** The floors serviced by this elevator.*/
    private boolean[] servicedFloors;

    /** The maximum capacity of the elevator in terms of passengers it can hold.*/
    private int elevatorCapacity;

    /** The current weight carried by the elevator.*/
    private int elevatorWeight;


    /**
     * Constructor for the Elevator class.
     *
     * @param elevatorNumber    The unique number identifying this elevator.
     * @param numberOfFloors    The total number of floors in the building.
     * @param elevatorCapacity  The maximum capacity of the elevator.
     */
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
        Arrays.fill(this.floorButtonStatus, false);
        this.servicedFloors = new boolean[numberOfFloors];
        Arrays.fill(this.servicedFloors, true);
        this.elevatorCapacity = elevatorCapacity;
        this.elevatorWeight = 0;
    }

    /**
     * Gets the elevator's number.
     * @return The elevator's number.
     */
    public int getElevatorNumber() {
        return elevatorNumber;
    }

    /**
     * Gets the current floor where the elevator is located.
     * @return The current floor of the elevator.
     */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Sets the current floor where the elevator is located.
     * @param currentFloor The current floor of the elevator.
     */
    public void setCurrentFloor(int currentFloor) {
        if(currentFloor > 0 && currentFloor < this.numberOfFloors) {
            this.currentFloor = currentFloor;
        }
    }

    /**
     * Gets the target floor towards which the elevator is moving.
     * @return The target floor of the elevator.
     */
    public int getTargetFloor() {
        return targetFloor;
    }

    /**
     * Sets the target floor towards which the elevator is moving.
     * @param targetFloor The target floor of the elevator.
     */
    public void setTargetFloor(int targetFloor) {
        if(targetFloor > 0 && targetFloor < this.numberOfFloors) {
            this.targetFloor = targetFloor;
        }
    }

    /**
     * Gets the direction in which the elevator is committed to moving.
     * @return The committed direction of the elevator.
     */
    public int getCommittedDirection() {
        return committedDirection;
    }

    /**
     * Sets the direction in which the elevator is committed to moving.
     * @param committedDirection The committed direction of the elevator.
     */
    public void setCommittedDirection(int committedDirection) {
        if(committedDirection < IElevator.ELEVATOR_DIRECTION_UP && committedDirection > IElevator.ELEVATOR_DIRECTION_UNCOMMITTED) {
            throw new IllegalArgumentException();
        }
        this.committedDirection = committedDirection;
    }

    /**
     * Gets the status of the elevator doors (open, closed, etc.).
     * @return The status of the elevator doors.
     */
    public int getDoorStatus() {
        return doorStatus;
    }

    /**
     * Sets the status of the elevator doors (open, closed, etc.).
     * @param doorStatus The status of the elevator doors.
     */
    public void setDoorStatus(int doorStatus) {
        if(doorStatus < IElevator.ELEVATOR_DOORS_OPEN && doorStatus > IElevator.ELEVATOR_DOORS_CLOSING) {
            throw new IllegalArgumentException();
        }
        this.doorStatus = doorStatus;
    }

    /**
     * Gets the speed of the elevator's movement.
     * @return The speed of the elevator.
     */
    public int getElevatorSpeed() {
        return elevatorSpeed;
    }

    /**
     * Sets the speed of the elevator's movement.
     * @param elevatorSpeed The speed of the elevator.
     */
    public void setElevatorSpeed(int elevatorSpeed) {
        this.elevatorSpeed = elevatorSpeed;
    }

    /**
     * Gets the acceleration of the elevator's movement.
     * @return The acceleration of the elevator.
     */
    public int getElevatorAccel() {
        return elevatorAccel;
    }

    /**
     * Sets the acceleration of the elevator's movement.
     * @param elevatorAccel The acceleration of the elevator.
     */
    public void setElevatorAccel(int elevatorAccel) {
        this.elevatorAccel = elevatorAccel;
    }

    /**
     * Gets the status of floor buttons inside the elevator (pressed or not) for a specific floor.
     * @param floor The floor number to check the button status.
     * @return The status of the floor button inside the elevator for the specified floor.
     */
    public boolean getFloorButtonStatus(int floor) {
        if(floor < 0 && floor >= this.numberOfFloors) {
            throw new IllegalArgumentException();
        }
        return floorButtonStatus[floor];
    }

    /**
     * Sets the status of floor buttons inside the elevator (pressed or not) for all floors.
     * @param floor The floor number to set the button status.
     * @param floorButtonStatus The array representing the status of floor buttons inside the elevator.
     */
    public void setFloorButtonStatus(int floor, boolean floorButtonStatus) {
        if(floor < 0 && floor >= this.numberOfFloors) {
            throw new IllegalArgumentException();
        }
        this.floorButtonStatus[floor] = floorButtonStatus;
    }

    /**
     * Gets the floors serviced by this elevator.
     * @param floor The floor number to check if it is serviced by the elevator.
     * @return True if the elevator services the specified floor, false otherwise.
     */
    public boolean getServicedFloors(int floor) {
        if(floor < 0 && floor >= this.numberOfFloors) {
            throw new IllegalArgumentException();
        }
        return servicedFloors[floor];
    }

    /**
     * Sets whether the elevator services a particular floor or not.
     * @param floor    The floor number to set the service status.
     * @param service  True if the elevator should service the floor, false otherwise.
     */
    public void setServicesFloor(int floor, boolean service) {
        if(floor < 0 && floor >= this.numberOfFloors) {
            throw new IllegalArgumentException();
        }
        this.servicedFloors[floor] = service;
    }

    /**
     * Gets the maximum capacity of the elevator in terms of passengers it can hold.
     * @return The maximum capacity of the elevator.
     */
    public int getElevatorCapacity() {
        return elevatorCapacity;
    }

    /**
     * Gets the current weight carried by the elevator.
     * @return The current weight of the elevator.
     */
    public int getElevatorWeight() {
        return elevatorWeight;
    }

    /**
     * Sets the current weight carried by the elevator.
     * @param elevatorWeight The weight to be set for the elevator.
     */
    public void setElevatorWeight(int elevatorWeight) {
        if(elevatorWeight < 0) {
            throw new IllegalArgumentException();
        }
        this.elevatorWeight = elevatorWeight;
    }
}

