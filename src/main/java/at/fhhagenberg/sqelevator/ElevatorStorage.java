package at.fhhagenberg.sqelevator;

public class ElevatorStorage {
    private int currentFloor = 0;
    private int targetFloor = 0;
    private int committedDirection = ELEVATOR_DIRECTION_UNCOMMITTED;
    private int doorStatus = ELEVATOR_DOORS_CLOSED;
    private boolean[] floorButtonStatus;

    public final static int ELEVATOR_DIRECTION_UP = 0;
    public final static int ELEVATOR_DIRECTION_DOWN = 1;
    public final static int ELEVATOR_DIRECTION_UNCOMMITTED = 2;
    public final static int ELEVATOR_DOORS_OPEN = 1;
    public final static int ELEVATOR_DOORS_CLOSED = 2;
    public final static int ELEVATOR_DOORS_OPENING = 3;
    public final static int ELEVATOR_DOORS_CLOSING = 4;

    public ElevatorStorage(int numberOfFloors) {
        floorButtonStatus = new boolean[numberOfFloors];
    }

    public int getCurrentFloor()
    {
        return currentFloor;
    }

    public void setCurrentFloor(int newFloor)
    {
        currentFloor = newFloor;
    }

    public int getTargetFloor()
    {
        return targetFloor;
    }

    public void setTargetFloor(int floor)
    {
        targetFloor = floor;
    }

    public int getCommittedDirection()
    {
        return committedDirection;
    }

    public void setCommittedDirection(int direction)
    {
        committedDirection = direction;
    }

    public int getDoorStatus()
    {
        return doorStatus;
    }

    public void setDoorStatus(int status)
    {
        doorStatus = status;
    }

    public boolean[] getFloorButtonStatus()
    {
        return floorButtonStatus;
    }

    public void setFloorButtonStatus(int floor, boolean status)
    {
        floorButtonStatus[floor] = status;
    }
}
