package uet.oop.bomberman.entities.Enemy;

import javafx.scene.image.Image;
import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.entities.World.Bomber;
import uet.oop.bomberman.entities.World.Brick;
import uet.oop.bomberman.entities.World.Entity;
import uet.oop.bomberman.entities.World.Wall;
import uet.oop.bomberman.graphics.Sprite;

import java.util.Random;

import static uet.oop.bomberman.GameEngine.BombermanGame.HEIGHT;
import static uet.oop.bomberman.GameEngine.BombermanGame.WIDTH;

public class Balloon extends Enemy {
    private String direction;
    private final Random random = new Random();

    public Balloon(int x, int y, Image img, GameManager gameManager) {
        super(x, y, img, gameManager);
        setRandomDirection();
    }

    private void setRandomDirection() {
        String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
        direction = directions[random.nextInt(directions.length)];
    }

    @Override
    public void update() {
        // Ensure movement stays within map boundaries
        int newX = x;
        int newY = y;

        // Check map boundaries and calculate new position
        switch (direction) {
            case "UP":
                if (y - speed >= 0) {
                    newY -= speed;
                    changeImage(Sprite.movingSprite(Sprite.balloom_left1, Sprite.balloom_left2, Sprite.balloom_left3, (int) System.currentTimeMillis() / 100, 20).getFxImage());
                } else {
                    setRandomDirection(); // Immediately change direction if hitting map boundary
                }
                break;
            case "DOWN":
                if (y + speed < HEIGHT * Sprite.SCALED_SIZE) {
                    newY += speed;
                    changeImage(Sprite.movingSprite(Sprite.balloom_right1, Sprite.balloom_right2, Sprite.balloom_right3, (int) System.currentTimeMillis() / 100, 20).getFxImage());
                } else {
                    setRandomDirection(); // Immediately change direction if hitting map boundary
                }
                break;
            case "LEFT":
                if (x - speed >= 0) {
                    newX -= speed;
                    changeImage(Sprite.movingSprite(Sprite.balloom_left1, Sprite.balloom_left2, Sprite.balloom_left3, (int) System.currentTimeMillis() / 100, 20).getFxImage());
                } else {
                    setRandomDirection(); // Immediately change direction if hitting map boundary
                }
                break;
            case "RIGHT":
                if (x + speed < WIDTH * Sprite.SCALED_SIZE) {
                    newX += speed;
                    changeImage(Sprite.movingSprite(Sprite.balloom_right1, Sprite.balloom_right2, Sprite.balloom_right3, (int) System.currentTimeMillis() / 100, 20).getFxImage());
                } else {
                    setRandomDirection(); // Immediately change direction if hitting map boundary
                }
                break;
        }

        // Check for collisions with walls and bricks
        if (!checkCollision(newX, newY)) {
            x = newX;
            y = newY;
        } else {
            // Immediately change direction upon hitting a wall or brick
            setRandomDirection();
        }

        // Check collision with Bomber
        Bomber bomber = findBomber();
        if (bomber != null && checkCollisionWithBomber(newX, newY, bomber)) {
            System.out.println("Balloon caught Bomber! Game Over.");
            // TODO: Add game over logic (e.g., stop timer, show message)
        }
    }

    private Bomber findBomber() {
        for (Entity entity : gameManager.getEntities()) {
            if (entity instanceof Bomber) {
                return (Bomber) entity;
            }
        }
        return null;
    }

//    public boolean checkCollision(int newX, int newY) {
//        int scaledSize = Sprite.SCALED_SIZE;
//        for (Entity entity : gameManager.getStillObjects()) {
//            if (entity instanceof Wall || entity instanceof Brick) {
//                if (newX < entity.getX() + scaledSize &&
//                        newX + scaledSize > entity.getX() &&
//                        newY < entity.getY() + scaledSize &&
//                        newY + scaledSize > entity.getY()) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    private boolean checkCollisionWithBomber(int newX, int newY, Bomber bomber) {
        int scaledSize = Sprite.SCALED_SIZE;
        return newX < bomber.getX() + scaledSize &&
                newX + scaledSize > bomber.getX() &&
                newY < bomber.getY() + scaledSize &&
                newY + scaledSize > bomber.getY();
    }
}