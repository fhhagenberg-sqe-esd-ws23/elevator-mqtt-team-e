package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import sqelevator.IElevator;

public class BuildingTest {

    private IElevator building;

    @BeforeEach
    public void SetUp() {
        building = new Building(3, 10, 6);
    }

    @Test
    void testConstructor() throws RemoteException {
        building = new Building(0, 10, 7);
        assertEquals(0, building.getElevatorNum());

        building = new Building(3, 0, 7);
        assertEquals(0, building.getFloorNum());

        building = new Building(3, 10, 6);
        assertEquals(6, building.getFloorHeight());
    }

    @Test
    void testInvalidConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new Building(-1, 10, 3));
        assertThrows(IllegalArgumentException.class, () -> new Building(3, -1, 3));
        assertThrows(IllegalArgumentException.class, () -> new Building(3, 10, 5));
    }

    @Test
    void testGetCommittedDirection() throws RemoteException {
        assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, building.getCommittedDirection(0));
    }

    @Test
    void testGetCommittedDirectionInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getCommittedDirection(-1));
        assertThrows(IllegalArgumentException.class, () -> building.getCommittedDirection(3));
    }

    @Test
    void testGetElevatorAccel() throws RemoteException {
        assertEquals(0, building.getElevatorAccel(1));
    }

    @Test
    void testGetElevatorAccelInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorAccel(-1));
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorAccel(3));
    }

    @Test
    void testGetElevatorButton() throws RemoteException {
        assertFalse(building.getElevatorButton(2, 5));
    }

    @Test
    void testGetElevatorButtonInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorButton(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorButton(3, 1));
    }

    @Test
    void testGetElevatorDoorStatus() throws RemoteException {
        assertEquals(IElevator.ELEVATOR_DOORS_CLOSED, building.getElevatorDoorStatus(0));
    }

    @Test
    void testGetElevatorDoorStatusInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorDoorStatus(-1));
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorDoorStatus(3));
    }

    @Test
    void testGetElevatorFloor() throws RemoteException {
        assertEquals(0, building.getElevatorFloor(1));
    }

    @Test
    void testGetElevatorFloorInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorFloor(-1));
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorFloor(3));
    }

    @Test
    void testGetElevatorNum() throws RemoteException {
        assertEquals(3, building.getElevatorNum());
    }

    @Test
    void testGetElevatorPosition() throws RemoteException {
        assertEquals(0, building.getElevatorPosition(2));
    }

    @Test
    void testGetElevatorPositionInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorPosition(-1));
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorPosition(3));
    }

    @Test
    void testGetElevatorSpeed() throws RemoteException {
        assertEquals(0, building.getElevatorSpeed(0));
    }

    @Test
    void testGetElevatorSpeedInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorSpeed(-1));
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorSpeed(3));
    }

    @Test
    void testGetElevatorWeight() throws RemoteException {
        assertEquals(0, building.getElevatorWeight(2));
    }

    @Test
    void testGetElevatorWeightInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorWeight(-1));
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorWeight(3));
    }

    @Test
    void testGetElevatorCapacity() throws RemoteException {
        assertEquals(5, building.getElevatorCapacity(1));
    }

    @Test
    void testGetElevatorCapacityInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorCapacity(-1));
        assertThrows(IllegalArgumentException.class, () -> building.getElevatorCapacity(3));
    }

    @Test
    void testGetFloorButtonDown() throws RemoteException {
        assertFalse(building.getFloorButtonDown(4));
    }

    @Test
    void testGetFloorButtonDownInvalidFloor() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getFloorButtonDown(-1));
        assertThrows(IllegalArgumentException.class, () -> building.getFloorButtonDown(10));
    }

    @Test
    void testGetFloorButtonUp() throws RemoteException {
        assertFalse(building.getFloorButtonUp(7));
    }

    @Test
    void testGetFloorButtonUpInvalidFloor() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getFloorButtonUp(-1));
        assertThrows(IllegalArgumentException.class, () -> building.getFloorButtonUp(10));
    }

    @Test
    void testGetFloorHeight() throws RemoteException {
        assertEquals(6, building.getFloorHeight());
    }

    @Test
    void testGetFloorNum() throws RemoteException {
        assertEquals(10, building.getFloorNum());
    }

    @Test
    void testGetServicesFloors() throws RemoteException {
        assertTrue(building.getServicesFloors(0, 8));
    }

    @Test
    void testGetServicesFloorsInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getServicesFloors(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> building.getServicesFloors(3, 1));
    }

    @Test
    void testGetServicesFloorsInvalidFloor() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getServicesFloors(2, -1));
        assertThrows(IllegalArgumentException.class, () -> building.getServicesFloors(2, 10));
    }

    @Test
    void testGetTarget() throws RemoteException {
        assertEquals(0, building.getTarget(2));
    }

    @Test
    void testGetTargetInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.getTarget(-1));
        assertThrows(IllegalArgumentException.class, () -> building.getTarget(3));
    }

    @Test
    void testSetCommittedDirection() throws RemoteException {
        building.setCommittedDirection(1, IElevator.ELEVATOR_DIRECTION_DOWN);
        assertEquals(IElevator.ELEVATOR_DIRECTION_DOWN, building.getCommittedDirection(1));
    }

    @Test
    void testSetCommittedDirectionInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.setCommittedDirection(-1, IElevator.ELEVATOR_DIRECTION_DOWN));
        assertThrows(IllegalArgumentException.class, () -> building.setCommittedDirection(3, IElevator.ELEVATOR_DIRECTION_DOWN));
    }

    @Test
    void testSetServicesFloors() throws RemoteException {
        building.setServicesFloors(2, 5, false);
        assertFalse(building.getServicesFloors(2, 5));
    }

    @Test
    void testSetServicesFloorsInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.setServicesFloors(-1, 5, false));
        assertThrows(IllegalArgumentException.class, () -> building.setServicesFloors(3, 5, true));
    }

    @Test
    void testSetTarget() throws RemoteException {
        building.setTarget(0, 3);
        assertEquals(3, building.getTarget(0));
    }

    @Test
    void testSetTargetInvalidElevator() throws RemoteException {
        assertThrows(IllegalArgumentException.class, () -> building.setTarget(-1, 5));
        assertThrows(IllegalArgumentException.class, () -> building.setTarget(3, 5));
    }

    @Test
    void testGetClockTick() throws RemoteException {
        assertEquals(0, building.getClockTick());
    }
}
