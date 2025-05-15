package uet.oop.bomberman.entities.Enemy;

import javafx.scene.image.Image;
import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.entities.World.Entity;

public abstract class Enemy extends Entity {
    protected int speed;
    protected GameManager gameManager;

    public Enemy(int x, int y, Image img, GameManager gameManager) {
        super(x, y, img);
        this.gameManager = gameManager;
        this.speed = 1; // Default speed, can be overridden by subclasses
    }

    public abstract void update();

    public void move(int dx, int dy) {
        x += dx * speed;
        y += dy * speed;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    // Method to change the image
    protected void changeImage(Image newImg) {
        this.img = newImg;
    }
}