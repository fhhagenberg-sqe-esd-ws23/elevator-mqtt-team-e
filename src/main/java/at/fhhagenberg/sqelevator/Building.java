package at.fhhagenberg.sqelevator;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a building housing multiple elevators and floors.
 */
public class Building implements IElevator {

    /** The maximum capacity of each elevator in the building. */
    private final int capacity = 5;

    /** The list of elevator objects present in the building.*/
    private List<Elevator> elevators;

    /** The list of floor objects present in the building.*/
    private List<Floor> floors;

    /** The height of each floor in the building.*/
    private final int floorHeight;

    /** The total number of floors in the building.*/
    private final int numberOfFloors;

    /** The total number of elevators in the building.*/
    private final int numberOfElevators;

    /** The current clock tick value for the building.*/
    private final int clockTick;

    /**
     * Constructor for the Building class.
     *
     * @param numberOfElevators The number of elevators in the building.
     * @param numberOfFloors    The number of floors in the building.
     * @param floorHeight       The height of each floor in the building.
     */
    public Building(int numberOfElevators, int numberOfFloors, int floorHeight) {

        // members
        this.floorHeight = floorHeight;
        this.numberOfFloors = numberOfFloors;
        this.numberOfElevators = numberOfElevators;
        this.clockTick = 0;

        this.elevators = new ArrayList<Elevator>(numberOfElevators);
        this.floors = new ArrayList<Floor>(numberOfFloors);

        // add elevators to building
        for(int i = 0; i < numberOfElevators; i++) {
            this.elevators.add(new Elevator(i, numberOfFloors, capacity));
        }


        for(int i = 0; i < numberOfFloors; i++) {
            this.floors.add(new Floor());
        }
    }

    /**
     * Checks if the elevator number is valid.
     *
     * @param elevatorNumber The number of the elevator to be checked.
     * @throws IndexOutOfBoundsException if the elevator number is invalid.
     */
    private void checkElevatorNumber(int elevatorNumber) throws IndexOutOfBoundsException {
        if(elevatorNumber >= this.numberOfElevators) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Checks if the elevator number is valid.
     *
     * @param floor The number of the floor to be checked.
     * @throws IndexOutOfBoundsException if the floor number is invalid.
     */
    private void checkFloorNumber(int floor) throws IndexOutOfBoundsException {
        if(floor >= this.numberOfFloors) {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCommittedDirection(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getCommittedDirection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElevatorAccel(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getElevatorAccel();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getElevatorButton(int elevatorNumber, int floor) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        if(floor < this.numberOfFloors) {
            return this.elevators.get(elevatorNumber).getFloorButtonStatus(floor);
        }
        return false;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getElevatorDoorStatus(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);

        return this.elevators.get(elevatorNumber).getDoorStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElevatorFloor(int elevatorNumber) throws RemoteException {
        return this.elevators.get(elevatorNumber).getCurrentFloor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElevatorNum() throws RemoteException {
        checkElevatorNumber(0);
        return this.numberOfElevators;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElevatorPosition(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getCurrentFloor() * this.floorHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElevatorSpeed(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getElevatorSpeed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElevatorWeight(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getElevatorWeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElevatorCapacity(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getElevatorCapacity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getFloorButtonDown(int floor) throws RemoteException {
        checkFloorNumber(floor);
        return this.floors.get(floor).isDownButtonPressed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getFloorButtonUp(int floor) throws RemoteException {
        checkFloorNumber(floor);
        return this.floors.get(floor).isUpButtonPressed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFloorHeight() throws RemoteException {
        return this.floorHeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFloorNum() throws RemoteException {
        return this.numberOfFloors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getServicesFloors(int elevatorNumber, int floor) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getServicedFloors(floor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTarget(int elevatorNumber) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        return this.elevators.get(elevatorNumber).getTargetFloor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCommittedDirection(int elevatorNumber, int direction) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        this.elevators.get(elevatorNumber).setCommittedDirection(direction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServicesFloors(int elevatorNumber, int floor, boolean service) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        if(floor < this.numberOfFloors) {
            this.elevators.get(elevatorNumber).setServicesFloor(floor, service);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTarget(int elevatorNumber, int target) throws RemoteException {
        checkElevatorNumber(elevatorNumber);
        this.elevators.get(elevatorNumber).setTargetFloor(target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getClockTick() throws RemoteException {
        return this.clockTick;
    }
}
