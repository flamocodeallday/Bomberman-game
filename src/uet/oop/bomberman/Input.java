package uet.oop.bomberman;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class Input {
    private boolean up, down ,left , right, spacePressed, spaceJustPressed, escapePressed, escapeJustPressed;

    public void handlePressed(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                up = true;
                break;
            case S:
                down = true;
                break;
            case D:
                right = true;
                break;
            case A:
                left = true;
                break;
            case SPACE:
                if (!spacePressed) {
                    spacePressed = true;
                    spaceJustPressed = true;
                }
                break;
            case ESCAPE:
                if (!escapePressed) {
                    escapePressed = true;
                    escapeJustPressed = true;
                }
        }
    }

    public void handleReleased(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                up = false;
                break;
            case S:
                down = false;
                break;
            case D:
                right = false;
                break;
            case A:
                left = false;
                break;
            case SPACE:
                spacePressed = false;
                spaceJustPressed = false;
                break;
            case ESCAPE:
                escapePressed = false;
                escapeJustPressed = false;
        }
    }

    public boolean isUp() { return up; }
    public boolean isDown() { return down; }
    public boolean isLeft() { return left; }
    public boolean isRight() { return right; }
    public boolean isSpaceJustPressed() {
        boolean result = spaceJustPressed;
        spaceJustPressed = false; // Reset sau khi gọi
        return result;
    }
    public boolean isEscapeJustPressed() {
        boolean result = escapeJustPressed;
        escapeJustPressed = false;
        return result;
    }

}
