package at.fhhagenberg.sqelevator;

import java.rmi.RemoteException;

import java.util.List;

public class Building implements IElevator {

    private final int capacity = 5;
    private List<Elevator> elevators;
    private List<Floor> floors;

    private final int floorHeight;

    private final int numberOfFloors;

    private final int numberOfElevators;

    private final int clockTick;

    public Building(int numberOfElevators, int numberOfFloors, int floorHeight) {

        // members
        this.floorHeight = floorHeight;
        this.numberOfFloors = numberOfFloors;
        this.numberOfElevators = numberOfElevators;
        this.clockTick = 0;

        // add elevators to building
        for(int i = 0; i < numberOfElevators; i++) {
            this.elevators.add(new Elevator(i, numberOfFloors, capacity));
        }

        for(int i = 0; i < numberOfFloors; i++) {
            this.floors.add(new Floor());
        }
    }

    private void checkElevatorNumber(int elevatorNumber) throws IndexOutOfBoundsException {
        if(elevatorNumber >= this.numberOfFloors) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public int getCommittedDirection(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getCommittedDirection();
    }

    @Override
    public int getElevatorAccel(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getElevatorAccel();
    }

    @Override
    public boolean getElevatorButton(int elevatorNumber, int floor) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        if(floor < this.numberOfFloors) {
            return this.elevators.get(elevatorNumber).getFloorButtonStatus(floor);
        }
        return false;
    }

    @Override
    public int getElevatorDoorStatus(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);

        return this.elevators.get(elevatorNumber).getDoorStatus();
    }

    @Override
    public int getElevatorFloor(int elevatorNumber) throws RemoteException {
        return this.elevators.get(elevatorNumber).getCurrentFloor();
    }

    @Override
    public int getElevatorNum() throws RemoteException {
        return this.numberOfElevators;
    }

    @Override
    public int getElevatorPosition(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getCurrentFloor() * this.floorHeight;
    }

    @Override
    public int getElevatorSpeed(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getElevatorSpeed();
    }

    @Override
    public int getElevatorWeight(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getElevatorWeight();
    }

    @Override
    public int getElevatorCapacity(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getElevatorCapacity();
    }

    @Override
    public boolean getFloorButtonDown(int floor) throws RemoteException {

        return false;
    }

    @Override
    public boolean getFloorButtonUp(int floor) throws RemoteException {
        return false;
    }

    @Override
    public int getFloorHeight() throws RemoteException {
        return this.floorHeight;
    }

    @Override
    public int getFloorNum() throws RemoteException {
        return this.numberOfFloors;
    }

    @Override
    public boolean getServicesFloors(int elevatorNumber, int floor) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getServicedFloors(floor);
    }

    @Override
    public int getTarget(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getTargetFloor();
    }

    @Override
    public void setCommittedDirection(int elevatorNumber, int direction) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        this.elevators.get(elevatorNumber).setCommittedDirection(direction);
    }

    @Override
    public void setServicesFloors(int elevatorNumber, int floor, boolean service) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        if(floor < this.numberOfFloors) {
            this.elevators.get(elevatorNumber).setServicesFloor(floor, service);
        }
    }

    @Override
    public void setTarget(int elevatorNumber, int target) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        this.elevators.get(elevatorNumber).setTargetFloor(target);
    }

    @Override
    public long getClockTick() throws RemoteException {
        return this.clockTick;
    }
}
