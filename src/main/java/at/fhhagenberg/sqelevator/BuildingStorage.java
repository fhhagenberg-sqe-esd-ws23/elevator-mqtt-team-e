package at.fhhagenberg.sqelevator;

import java.util.ArrayList;
import java.util.List;

public class BuildingStorage {
    private final List<ElevatorStorage> elevators;

    private final List<Floor> floors;

    private final int numberOfFloors;

    private final int numberOfElevators;

    public BuildingStorage(int floorNum, int elevatorNum)
    {
        numberOfFloors = floorNum;
        numberOfElevators = elevatorNum;

        floors = new ArrayList<>();
        elevators = new ArrayList<>();

        for(int i = 0; i < numberOfFloors; i++)
        {
            floors.add(new Floor());
        }

        for(int i = 0; i < numberOfElevators; i++)
        {
            elevators.add(new ElevatorStorage(numberOfFloors));
        }
    }

    public int getElevatorNum()
    {
        return numberOfElevators;
    }

    public int getFloorNum()
    {
        return numberOfFloors;
    }

    public boolean getFloorState(int floor, boolean up)
    {
        return up ? this.floors.get(floor).isUpButtonPressed() : this.floors.get(floor).isDownButtonPressed();
    }

    public void setFloorState(int floor, boolean up, boolean pressed)
    {
        if(up){
            this.floors.get(floor).setUpPressed(pressed);
        } else {
            this.floors.get(floor).setDownPressed(pressed);
        }
    }

    public int getCurrentFloor(int elevator)
    {
        return elevators.get(elevator).getCurrentFloor();
    }

    public void setCurrentFloor(int elevator, int newFloor)
    {
        elevators.get(elevator).setCurrentFloor(newFloor);
    }

    public int getTargetFloor(int elevator)
    {
        return elevators.get(elevator).getTargetFloor();
    }

    public void setTargetFloor(int elevator, int floor)
    {
        elevators.get(elevator).setTargetFloor(floor);
    }

    public int getCommittedDirection(int elevator)
    {
        return elevators.get(elevator).getCommittedDirection();
    }

    public void setCommittedDirection(int elevator, int direction)
    {
        elevators.get(elevator).setCommittedDirection(direction);
    }

    public int getDoorStatus(int elevator)
    {
        return elevators.get(elevator).getDoorStatus();
    }

    public void setDoorStatus(int elevator, int status)
    {
        elevators.get(elevator).setDoorStatus(status);
    }

    // Return Floorbuttonstatus for all Floors
    public boolean[] getFloorButtonStatus(int elevator)
    {
        return elevators.get(elevator).getFloorButtonStatus();
    }

    public void setFloorButtonStatus(int elevator, int floor, boolean status)
    {
        elevators.get(elevator).setFloorButtonStatus(floor, status);
    }
}
