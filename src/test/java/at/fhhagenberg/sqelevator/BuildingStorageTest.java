package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BuildingStorageTest {

    @Test
    void testBuildingStorageConstructor() {
        BuildingStorage buildingStorage = new BuildingStorage(10, 3);
        assertEquals(10, buildingStorage.getFloorNum());
        assertEquals(3, buildingStorage.getElevatorNum());
    }

    @Test
    void testGetElevatorNum() {
        BuildingStorage buildingStorage = new BuildingStorage(10, 3);
        assertEquals(3, buildingStorage.getElevatorNum());
    }

    @Test
    void testGetFloorNum() {
        BuildingStorage buildingStorage = new BuildingStorage(10, 3);
        assertEquals(10, buildingStorage.getFloorNum());
    }

    @Test
    void testSetGetFloorState() {
        BuildingStorage buildingStorage = new BuildingStorage(5, 2);
        buildingStorage.setFloorState(2, true, true);
        assertTrue(buildingStorage.getFloorState(2, true));

        buildingStorage.setFloorState(2, true, false);
        assertFalse(buildingStorage.getFloorState(2, true));

        buildingStorage.setFloorState(2, false, true);
        assertTrue(buildingStorage.getFloorState(2, false));

        buildingStorage.setFloorState(2, false, false);
        assertFalse(buildingStorage.getFloorState(2, false));
    }

    @Test
    void testSetGetCurrentFloorForElevator() {
        BuildingStorage buildingStorage = new BuildingStorage(5, 2);
        buildingStorage.setCurrentFloor(1, 3);
        assertEquals(3, buildingStorage.getCurrentFloor(1));
    }

    @Test
    void testSetGetTargetFloorForElevator() {
        BuildingStorage buildingStorage = new BuildingStorage(5, 2);
        buildingStorage.setTargetFloor(1, 4);
        assertEquals(4, buildingStorage.getTargetFloor(1));
    }

    @Test
    void testSetGetCommittedDirectionForElevator() {
        BuildingStorage buildingStorage = new BuildingStorage(5, 2);
        buildingStorage.setCommittedDirection(1, ElevatorStorage.ELEVATOR_DIRECTION_UP);
        assertEquals(ElevatorStorage.ELEVATOR_DIRECTION_UP, buildingStorage.getCommittedDirection(1));

        buildingStorage.setCommittedDirection(1, ElevatorStorage.ELEVATOR_DIRECTION_DOWN);
        assertEquals(ElevatorStorage.ELEVATOR_DIRECTION_DOWN, buildingStorage.getCommittedDirection(1));

        buildingStorage.setCommittedDirection(1, ElevatorStorage.ELEVATOR_DIRECTION_UNCOMMITTED);
        assertEquals(ElevatorStorage.ELEVATOR_DIRECTION_UNCOMMITTED, buildingStorage.getCommittedDirection(1));
    }

    @Test
    void testSetGetDoorStatusForElevator() {
        BuildingStorage buildingStorage = new BuildingStorage(5, 2);
        buildingStorage.setDoorStatus(1, ElevatorStorage.ELEVATOR_DOORS_OPEN);
        assertEquals(ElevatorStorage.ELEVATOR_DOORS_OPEN, buildingStorage.getDoorStatus(1));

        buildingStorage.setDoorStatus(1, ElevatorStorage.ELEVATOR_DOORS_CLOSED);
        assertEquals(ElevatorStorage.ELEVATOR_DOORS_CLOSED, buildingStorage.getDoorStatus(1));

        buildingStorage.setDoorStatus(1, ElevatorStorage.ELEVATOR_DOORS_OPENING);
        assertEquals(ElevatorStorage.ELEVATOR_DOORS_OPENING, buildingStorage.getDoorStatus(1));

        buildingStorage.setDoorStatus(1, ElevatorStorage.ELEVATOR_DOORS_CLOSING);
        assertEquals(ElevatorStorage.ELEVATOR_DOORS_CLOSING, buildingStorage.getDoorStatus(1));
    }

    @Test
    void testSetGetFloorButtonStatusForElevator() {
        BuildingStorage buildingStorage = new BuildingStorage(5, 2);
        buildingStorage.setFloorButtonStatus(1, 2, true);
        boolean[] floorButtonStatus = buildingStorage.getFloorButtonStatus(1);
        assertTrue(floorButtonStatus[2]);
    }
}
