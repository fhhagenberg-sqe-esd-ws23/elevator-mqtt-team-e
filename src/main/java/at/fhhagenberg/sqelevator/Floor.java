package at.fhhagenberg.sqelevator;

/**
 * Represents a floor in the building.
 */
public class Floor {
    /** Represents whether the 'Up' button on the floor is pressed or not.*/
    private boolean upPressed;

    /** Represents whether the 'Down' button on the floor is pressed or not.*/
    private boolean downPressed;

    /**
     * Constructor for the Floor class.
     * Initializes the up and down button statuses as 'not pressed' by default.
     */
    public Floor() {
        this.upPressed = false;
        this.downPressed = false;
    }

    /**
     * Checks if the 'Up' button on the floor is pressed.
     * @return True if the 'Up' button is pressed, otherwise false.
     */
    public boolean isUpButtonPressed() {
        return this.upPressed;
    }

    /**
     * Checks if the 'Down' button on the floor is pressed.
     * @return True if the 'Down' button is pressed, otherwise false.
     */
    public boolean isDownButtonPressed() {
        return this.downPressed;
    }

    /**
     * Sets the status of the 'Up' button on the floor.
     * @param pressed The status of the 'Up' button (pressed or not).
     */
    public void setUpPressed(boolean pressed) {
        this.upPressed = pressed;
    }
    
    /**
     * Sets the status of the 'Down' button on the floor.
     * @param pressed The status of the 'Down' button (pressed or not).
     */
    public void setDownPressed(boolean pressed) {
        this.downPressed = pressed;
    }
}
