package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ElevatorStorageTest {

    @Test
    void testElevatorStorageConstructor() {
        ElevatorStorage elevatorStorage = new ElevatorStorage(10);
        assertNotNull(elevatorStorage.getFloorButtonStatus());
        assertEquals(10, elevatorStorage.getFloorButtonStatus().length);
    }

    @Test
    void testSetGetCurrentFloor() {
        ElevatorStorage elevatorStorage = new ElevatorStorage(5);
        elevatorStorage.setCurrentFloor(3);
        assertEquals(3, elevatorStorage.getCurrentFloor());
    }

    @Test
    void testSetGetTargetFloor() {
        ElevatorStorage elevatorStorage = new ElevatorStorage(5);
        elevatorStorage.setTargetFloor(4);
        assertEquals(4, elevatorStorage.getTargetFloor());
    }

    @Test
    void testSetGetCommittedDirection() {
        ElevatorStorage elevatorStorage = new ElevatorStorage(5);
        elevatorStorage.setCommittedDirection(ElevatorStorage.ELEVATOR_DIRECTION_UP);
        assertEquals(ElevatorStorage.ELEVATOR_DIRECTION_UP, elevatorStorage.getCommittedDirection());

        elevatorStorage.setCommittedDirection(ElevatorStorage.ELEVATOR_DIRECTION_DOWN);
        assertEquals(ElevatorStorage.ELEVATOR_DIRECTION_DOWN, elevatorStorage.getCommittedDirection());

        elevatorStorage.setCommittedDirection(ElevatorStorage.ELEVATOR_DIRECTION_UNCOMMITTED);
        assertEquals(ElevatorStorage.ELEVATOR_DIRECTION_UNCOMMITTED, elevatorStorage.getCommittedDirection());
    }

    @Test
    void testSetGetDoorStatus() {
        ElevatorStorage elevatorStorage = new ElevatorStorage(5);
        elevatorStorage.setDoorStatus(ElevatorStorage.ELEVATOR_DOORS_OPEN);
        assertEquals(ElevatorStorage.ELEVATOR_DOORS_OPEN, elevatorStorage.getDoorStatus());

        elevatorStorage.setDoorStatus(ElevatorStorage.ELEVATOR_DOORS_CLOSED);
        assertEquals(ElevatorStorage.ELEVATOR_DOORS_CLOSED, elevatorStorage.getDoorStatus());
    }

    @Test
    void testSetGetFloorButtonStatus() {
        ElevatorStorage elevatorStorage = new ElevatorStorage(5);
        elevatorStorage.setFloorButtonStatus(2, true);
        assertTrue(elevatorStorage.getFloorButtonStatus()[2]);

        elevatorStorage.setFloorButtonStatus(2, false);
        assertFalse(elevatorStorage.getFloorButtonStatus()[2]);
    }

}
