package uet.oop.bomberman;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class Input {
    private boolean up, down ,left , right;

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
        }
    }

    public boolean isUp() { return up; }
    public boolean isDown() { return down; }
    public boolean isLeft() { return left; }
    public boolean isRight() { return right; }

}
