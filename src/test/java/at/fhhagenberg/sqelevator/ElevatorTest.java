package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class ElevatorTest {
    Elevator elevator;
    @BeforeEach
    void SetUp(){
        elevator = new Elevator(1, 10, 8);
    }
    @Test
    void testGetElevatorNumber() {
        assertEquals(1, elevator.getElevatorNumber());
    }

    @Test
    void testGetCurrentFloor() {
        assertEquals(0, elevator.getCurrentFloor());
    }

    @Test
    void testSetCurrentFloor() {
        elevator.setCurrentFloor(0);
        assertEquals(0, elevator.getCurrentFloor());
        elevator.setCurrentFloor(9);
        assertEquals(9, elevator.getCurrentFloor());
    }

    @Test
    void testSetCurrentFloorInvalid() {
        assertThrows(IllegalArgumentException.class, () -> elevator.setCurrentFloor(-1));
        assertThrows(IllegalArgumentException.class, () -> elevator.setCurrentFloor(10));
    }

    @Test
    void testGetTargetFloor() {
        assertEquals(0, elevator.getTargetFloor());
    }

    @Test
    void testSetTargetFloor() {
        elevator.setTargetFloor(0);
        assertEquals(0, elevator.getTargetFloor());
        elevator.setTargetFloor(9);
        assertEquals(9, elevator.getTargetFloor());
    }

    @Test
    void testSetTargetFloorInvalid() {
        assertThrows(IllegalArgumentException.class, () -> elevator.setTargetFloor(-1));
        assertThrows(IllegalArgumentException.class, () -> elevator.setTargetFloor(10));
    }

    @Test
    void testGetCommittedDirection() {
        assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, elevator.getCommittedDirection());
    }

    @Test
    void testSetCommittedDirection() {
        elevator.setCommittedDirection(IElevator.ELEVATOR_DIRECTION_UP);
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, elevator.getCommittedDirection());
        elevator.setCommittedDirection(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED);
        assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, elevator.getCommittedDirection());
    }

    @Test
    void testSetCommittedDirectionInvalid() {
        assertThrows(IllegalArgumentException.class, () -> elevator.setCommittedDirection(-1));
        assertThrows(IllegalArgumentException.class, () -> elevator.setCommittedDirection(3));
    }

    @Test
    void testGetDoorStatus() {
        assertEquals(IElevator.ELEVATOR_DOORS_CLOSED, elevator.getDoorStatus());
    }

    @Test
    void testSetDoorStatus() {
        elevator.setDoorStatus(IElevator.ELEVATOR_DOORS_OPEN);
        assertEquals(IElevator.ELEVATOR_DOORS_OPEN, elevator.getDoorStatus());

        elevator.setDoorStatus(IElevator.ELEVATOR_DOORS_CLOSING);
        assertEquals(IElevator.ELEVATOR_DOORS_CLOSING, elevator.getDoorStatus());
    }

    @Test
    void testSetDoorStatusInvalid() {
        assertThrows(IllegalArgumentException.class, () -> elevator.setDoorStatus(0));
        assertThrows(IllegalArgumentException.class, () -> elevator.setDoorStatus(5));
    }

    @Test
    void testGetElevatorSpeed() {
        assertEquals(0, elevator.getElevatorSpeed());
    }

    @Test
    void testSetElevatorSpeed() {
        elevator.setElevatorSpeed(5);
        assertEquals(5, elevator.getElevatorSpeed());
    }

    @Test
    void testGetElevatorAccel() {
        assertEquals(0, elevator.getElevatorAccel());
    }

    @Test
    void testSetElevatorAccel() {
        elevator.setElevatorAccel(2);
        assertEquals(2, elevator.getElevatorAccel());
    }

    @Test
    void testGetFloorButtonStatus() {
        assertFalse(elevator.getFloorButtonStatus(0));
        assertFalse(elevator.getFloorButtonStatus(9));
    }

    @Test
    void testGetFloorButtonStatusInvalidFloor() {
        assertThrows(IllegalArgumentException.class, () -> elevator.getFloorButtonStatus(-1));
        assertThrows(IllegalArgumentException.class, () -> elevator.getFloorButtonStatus(10));
    }

    @Test
    void testSetFloorButtonStatus() {
        elevator.setFloorButtonStatus(0, true);
        assertTrue(elevator.getFloorButtonStatus(0));

        elevator.setFloorButtonStatus(9, false);
        assertFalse(elevator.getFloorButtonStatus(9));
    }

    @Test
    void testSetFloorButtonStatusInvalid() {
        assertThrows(IllegalArgumentException.class, () -> elevator.setFloorButtonStatus(-1, true));
        assertThrows(IllegalArgumentException.class, () -> elevator.setFloorButtonStatus(10, false));
    }

    @Test
    void testGetServicedFloors() {
        assertTrue(elevator.getServicedFloors(0));
        assertTrue(elevator.getServicedFloors(9));
    }

    @Test
    void testGetServicedFloorsInvalidFloor() {
        assertThrows(IllegalArgumentException.class, () -> elevator.getServicedFloors(-1));
        assertThrows(IllegalArgumentException.class, () -> elevator.getServicedFloors(10));
    }

    @Test
    void testSetServicesFloor() {
        elevator.setServicesFloor(0, false);
        assertFalse(elevator.getServicedFloors(0));
        elevator.setServicesFloor(9, false);
        assertFalse(elevator.getServicedFloors(9));
    }

    @Test
    void testSetServicesFloorInvalidFloor() {
        assertThrows(IllegalArgumentException.class, () -> elevator.setServicesFloor(-1, true));
        assertThrows(IllegalArgumentException.class, () -> elevator.setServicesFloor(10, false));
    }

    @Test
    void testSetServicesFloorInvalid() {
        assertThrows(IllegalArgumentException.class, () -> elevator.setServicesFloor(-1, true));
        assertThrows(IllegalArgumentException.class, () -> elevator.setServicesFloor(-1, false));
        assertThrows(IllegalArgumentException.class, () -> elevator.setServicesFloor(10, true));
        assertThrows(IllegalArgumentException.class, () -> elevator.setServicesFloor(10, false));
    }

    @Test
    void testGetElevatorCapacity() {
        assertEquals(8, elevator.getElevatorCapacity());
    }

    @Test
    void testGetElevatorWeight() {
        assertEquals(0, elevator.getElevatorWeight());
    }

    @Test
    void testSetElevatorWeight() {
        elevator.setElevatorWeight(0);
        assertEquals(0, elevator.getElevatorWeight());
        elevator.setElevatorWeight(1);
        assertEquals(1, elevator.getElevatorWeight());
    }

    @Test
    void testSetElevatorWeightInvalid() {
        assertThrows(IllegalArgumentException.class, () -> elevator.setElevatorWeight(-1));
    }
}
