package at.fhhagenberg.sqelevator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyStore;

import static org.junit.jupiter.api.Assertions.*;

class FloorTest {
    private Floor floor;

    @BeforeEach
    public void setUp() {
        floor = new Floor();
    }

    @Test
    void testDefaultButtonStatus() {
        assertFalse(floor.isUpButtonPressed());
        assertFalse(floor.isDownButtonPressed());
    }

    @Test
    void testSetUpButtonPressed() {
        floor.setUpPressed(true);
        assertTrue(floor.isUpButtonPressed());
        assertFalse(floor.isDownButtonPressed());
    }

    @Test
    void testSetDownButtonPressed() {
        floor.setDownPressed(true);
        assertFalse(floor.isUpButtonPressed());
        assertTrue(floor.isDownButtonPressed());
    }

    @Test
    void testSetBothButtonsPressed() {
        floor.setUpPressed(true);
        floor.setDownPressed(true);
        assertTrue(floor.isUpButtonPressed());
        assertTrue(floor.isDownButtonPressed());
    }

    @Test
    void testResetButtonStatus() {
        floor.setUpPressed(true);
        floor.setDownPressed(true);

        floor.setUpPressed(false);
        floor.setDownPressed(false);

        assertFalse(floor.isUpButtonPressed());
        assertFalse(floor.isDownButtonPressed());
    }
}
