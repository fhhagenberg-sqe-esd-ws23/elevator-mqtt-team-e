package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ElevatorTest {
    @Test
    public void testCreation() {
        Elevator e = new Elevator(1,10,5);
        assertEquals(1, e.getElevatorNumber());
        assertEquals(5, e.getElevatorCapacity());
    }
}
