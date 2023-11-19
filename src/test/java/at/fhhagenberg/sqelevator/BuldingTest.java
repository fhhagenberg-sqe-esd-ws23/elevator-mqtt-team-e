package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

public class BuldingTest {
    @Test
    public void testCreation() throws RemoteException {
        Building b = new Building(3,10,5);
        assertEquals(3, b.getElevatorNum());
        assertEquals(10, b.getFloorNum());
        assertEquals(5, b.getFloorHeight());
    }
}
