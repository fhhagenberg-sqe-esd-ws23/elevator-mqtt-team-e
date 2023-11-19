package at.fhhagenberg.sqelevator;

public class Floor {
    private boolean upPressed;
    private boolean downPressed;

    public Floor() {
        this.upPressed = false;
        this.downPressed = false;
    }

    public boolean isUpButtonPressed() {
        return this.upPressed;
    }

    public boolean isDownButtonPressed() {
        return this.downPressed;
    }

    public void setUpPressed(boolean pressed) {
        this.upPressed = pressed;
    }


    public void setDownPressed(boolean pressed) {
        this.downPressed = pressed;
    }
}
