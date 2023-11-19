package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class BuldingTest {
    private IElevator building;

    @BeforeEach
    public void setUp() {
        building = new Building(3,10,5);
    }

    @Test
    public void TestCTOR() throws RemoteException {
        assertEquals(3, building.getElevatorNum());
        assertEquals(10, building.getFloorNum());
        assertEquals(5, building.getFloorHeight());
    }

    @Test
    public void testGetCommittedDirection() throws RemoteException {
        int elevatorNumber = 0;
        int committedDirection = building.getCommittedDirection(elevatorNumber);
        assertNotNull(committedDirection);
        assertTrue(committedDirection >= 0 && committedDirection <= 2);
    }

    @Test
    public void testGetElevatorAccel() throws RemoteException {
        int elevatorNumber = 0;
        int acceleration = building.getElevatorAccel(elevatorNumber);
        assertNotNull(acceleration);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetElevatorButton() throws RemoteException {
        int elevatorNumber = 0;
        int floor = 1;
        boolean buttonStatus = building.getElevatorButton(elevatorNumber, floor);
        // Assuming a certain behavior, replace with your actual implementation
        assertFalse(buttonStatus);
    }

    @Test
    public void testGetElevatorDoorStatus() throws RemoteException {
        int elevatorNumber = 0;
        int doorStatus = building.getElevatorDoorStatus(elevatorNumber);
        assertNotNull(doorStatus);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetElevatorFloor() throws RemoteException {
        int elevatorNumber = 0;
        int floor = building.getElevatorFloor(elevatorNumber);
        assertNotNull(floor);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetElevatorNum() throws RemoteException {
        int numElevators = building.getElevatorNum();
        assertNotNull(numElevators);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetElevatorPosition() throws RemoteException {
        int elevatorNumber = 0;
        int position = building.getElevatorPosition(elevatorNumber);
        assertNotNull(position);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetElevatorSpeed() throws RemoteException {
        int elevatorNumber = 0;
        int speed = building.getElevatorSpeed(elevatorNumber);
        assertNotNull(speed);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetElevatorWeight() throws RemoteException {
        int elevatorNumber = 0;
        int weight = building.getElevatorWeight(elevatorNumber);
        assertNotNull(weight);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetElevatorCapacity() throws RemoteException {
        int elevatorNumber = 0;
        int capacity = building.getElevatorCapacity(elevatorNumber);
        assertNotNull(capacity);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetFloorButtonDown() throws RemoteException {
        int floor = 1;
        boolean isButtonDown = building.getFloorButtonDown(floor);
        assertNotNull(isButtonDown);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetFloorButtonUp() throws RemoteException {
        int floor = 1;
        boolean isButtonUp = building.getFloorButtonUp(floor);
        assertNotNull(isButtonUp);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetFloorHeight() throws RemoteException {
        int floorHeight = building.getFloorHeight();
        assertNotNull(floorHeight);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetFloorNum() throws RemoteException {
        int numFloors = building.getFloorNum();
        assertNotNull(numFloors);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetServicesFloors() throws RemoteException {
        int elevatorNumber = 0;
        int floor = 1;
        boolean servicesFloors = building.getServicesFloors(elevatorNumber, floor);
        assertNotNull(servicesFloors);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testGetTarget() throws RemoteException {
        int elevatorNumber = 0;
        int target = building.getTarget(elevatorNumber);
        assertNotNull(target);
        // Add specific assertions based on your implementation's behavior
    }

    @Test
    public void testSetCommittedDirection() throws RemoteException {
        int elevatorNumber = 0;
        int direction = 0;
        building.setCommittedDirection(elevatorNumber, direction);
        // Check if the direction is set correctly, based on your implementation
        int newDirection = building.getCommittedDirection(elevatorNumber);
        assertEquals(direction, newDirection);
    }

    @Test
    public void testSetServicesFloors() throws RemoteException {
        int elevatorNumber = 0;
        int floor = 1;
        boolean service = true;
        building.setServicesFloors(elevatorNumber, floor, service);
        // Check if the floor service is set correctly, based on your implementation
        boolean newService = building.getServicesFloors(elevatorNumber, floor);
        assertEquals(service, newService);
    }

    @Test
    public void testSetTarget() throws RemoteException {
        int elevatorNumber = 0;
        int target = 2;
        building.setTarget(elevatorNumber, target);
        // Check if the target is set correctly, based on your implementation
        int newTarget = building.getTarget(elevatorNumber);
        assertEquals(target, newTarget);
    }

    @Test
    public void testGetClockTick() throws RemoteException {
        long clockTick = building.getClockTick();
        assertNotNull(clockTick);
        // Add specific assertions based on your implementation's behavior
    }


}
