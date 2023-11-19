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
        assertEquals(IElevator.ELEVATOR_DIRECTION_UNCOMMITTED, building.getCommittedDirection(0));
    }

    @Test
    public void testSetCommittedDirection() throws RemoteException {
        building.setCommittedDirection(0, IElevator.ELEVATOR_DIRECTION_UP);
        assertEquals(IElevator.ELEVATOR_DIRECTION_UP, building.getCommittedDirection(0));
    }

    @Test
    public void testSetServicesFloors() throws RemoteException {
        assertEquals(true, building.getServicesFloors(0, 1));
        building.setServicesFloors(0, 1, false);
        assertEquals(false, building.getServicesFloors(0, 1));
    }

    @Test
    public void testSetTarget() throws RemoteException {
        building.setTarget(0, 2);
        assertEquals(2, building.getTarget(0));
    }

    @Test
    public void testGetClockTick() throws RemoteException {
        assertEquals(0, building.getClockTick());
    }
}
