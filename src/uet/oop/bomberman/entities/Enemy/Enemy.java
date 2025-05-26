package uet.oop.bomberman.entities.Enemy;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.bomberman.GameEngine.BombermanGame;
import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.entities.World.Bomber;
import uet.oop.bomberman.entities.World.Entity;
import uet.oop.bomberman.entities.World.Wall;
import uet.oop.bomberman.entities.World.Brick;
import uet.oop.bomberman.graphics.Sprite;

import java.util.Random;

public abstract class Enemy extends Entity {
    protected double speed; // Now a protected field set in subclasses
    protected GameManager game;
    protected boolean isAlive = true;
    protected int deathAnimationCount = 30; // Duration of death animation
    protected int deathFrame = 0; // Current frame of death animation
    protected int direction; // 0: up, 1: right, 2: down, 3: left
    protected Random random = new Random();
    protected int animate = 0;
    protected final int MAX_ANIMATE = 7500;

    public Enemy(int xUnit, int yUnit, Image img, GameManager game) {
        super(xUnit, yUnit, img);
        this.game = game;
        this.direction = random.nextInt(4); // Random initial direction
    }

    @Override
    public void update() {
        if (!isAlive) {
            if (deathAnimationCount > 0) {
                deathAnimationCount--;
                if (deathAnimationCount % 10 == 0) {
                    deathFrame++;
                }
            } else {
                isRemoved = true; // Mark for removal after death animation
            }
            return;
        }

        animate();
        move();
        checkCollisionWithBomber();
    }

    protected void animate() {
        if (animate < MAX_ANIMATE) animate++;
        else animate = 0;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isAlive) {
            Image deadSprite = getDeathSprite();
            gc.drawImage(deadSprite, x, y);
            return;
        }
        gc.drawImage(img, x, y);
    }

    protected abstract Image getDeathSprite(); // Each enemy provides its death animation sprite
    protected abstract void chooseSprite(); // Each enemy updates its sprite based on direction

    protected void move() {
        double newX = x;
        double newY = y;

        switch (direction) {
            case 0: // Up
                newY -= speed;
                break;
            case 1: // Right
                newX += speed;
                break;
            case 2: // Down
                newY += speed;
                break;
            case 3: // Left
                newX -= speed;
                break;
        }

        if (canMove(newX, newY)) {
            x = (int) newX;
            y = (int) newY;
        } else {
            direction = random.nextInt(4); // Change direction if collision occurs
        }

        chooseSprite(); // Update sprite based on direction
    }

    protected boolean canMove(double newX, double newY) {
        int size = Sprite.SCALED_SIZE;
        int buffer = 6;

        double left = newX + buffer;
        double right = newX + size - buffer - 1;
        double top = newY + buffer;
        double bottom = newY + size - buffer - 1;

        int[][] checkUnit = {
                {(int) (left / size), (int) (top / size)},
                {(int) (right / size), (int) (top / size)},
                {(int) (left / size), (int) (bottom / size)},
                {(int) (right / size), (int) (bottom / size)},
        };

        for (int[] unit : checkUnit) {
            int checkX = unit[0];
            int checkY = unit[1];

            if (checkX < 0 || checkX >= BombermanGame.WIDTH || checkY < 0 || checkY >= BombermanGame.HEIGHT) {
                return false;
            }

            for (Entity entity : game.getStillObjects()) {
                int entityX = entity.getX() / size;
                int entityY = entity.getY() / size;

                if (entityX == checkX && entityY == checkY &&
                        (entity instanceof Wall || entity instanceof Brick)) {
                    double entityLeft = entity.getX();
                    double entityRight = entity.getX() + size - 1;
                    double entityTop = entity.getY();
                    double entityBottom = entity.getY() + size - 1;

                    if (!(right < entityLeft || left > entityRight || bottom < entityTop || top > entityBottom)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected void checkCollisionWithBomber() {
        for (Entity entity : game.getEntities()) {
            if (entity instanceof Bomber) {
                Bomber bomber = (Bomber) entity;
                if (bomber.isAlive() && this.intersects(bomber)) {
                    bomber.kill();

                }
            }
        }
    }

    public void die() {
        if (isAlive) {
            isAlive = false;
            deathAnimationCount = 30;
            deathFrame = 0;
        }
    }

    public boolean isAlive() {
        return isAlive;
    }
}