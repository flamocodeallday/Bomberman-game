package uet.oop.bomberman.entities.Enemy;

import javafx.scene.image.Image;
import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.graphics.Sprite;

public class Balloon extends Enemy {
    public Balloon(int xUnit, int yUnit, Image img, GameManager game) {
        super(xUnit, yUnit, img, game);
        this.speed = 1.0; // Fixed speed for Balloon
    }

    @Override
    protected Image getDeathSprite() {
        switch (deathFrame) {
            case 0:
                return Sprite.balloon_dead.getFxImage();
            case 1:
                return Sprite.balloon_dead.getFxImage();
            case 2:
            default:
                return Sprite.balloon_dead.getFxImage();
        }
    }

    @Override
    protected void chooseSprite() {
        switch (direction) {
            case 0: // Up
            case 2: // Down
                img = Sprite.movingSprite(Sprite.balloon_right1, Sprite.balloon_right2, Sprite.balloon_right3, animate, 20).getFxImage();
                break;
            case 1: // Right
                img = Sprite.movingSprite(Sprite.balloon_right1, Sprite.balloon_right2, Sprite.balloon_right3, animate, 20).getFxImage();
                break;
            case 3: // Left
                img = Sprite.movingSprite(Sprite.balloon_left1, Sprite.balloon_left2, Sprite.balloon_left3, animate, 20).getFxImage();
                break;
        }
    }
}