package uet.oop.bomberman.entities.Enemy;

import javafx.scene.image.Image;
import uet.oop.bomberman.entities.World.Bomber;
import uet.oop.bomberman.entities.World.Brick;
import uet.oop.bomberman.entities.World.Entity;
import uet.oop.bomberman.entities.World.Wall;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.GameEngine.GameManager;

import java.util.Random;

public class Balloon extends Entity {
    private final int speed = 1;
    private String direction;
    private final Random random = new Random();
    private int stuckCounter = 0;
    private static final int MAX_STUCK_FRAMES = 60;
    private GameManager game;

    public Balloon(int x, int y, Image img, GameManager game) {
        super(x, y, img);
        setRandomDirection();
        this.game = game;
    }

    private void setRandomDirection() {
        String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
        direction = directions[random.nextInt(directions.length)];
    }

    @Override
    public void update() {
        int newX = x;
        int newY = y;
        boolean moved = false;

        switch (direction) {
            case "UP":
                newY -= speed;
                img = Sprite.movingSprite(Sprite.balloom_left1, Sprite.balloom_left2, Sprite.balloom_left3, (int) System.currentTimeMillis() / 100, 20).getFxImage();
                break;
            case "DOWN":
                newY += speed;
                img = Sprite.movingSprite(Sprite.balloom_right1, Sprite.balloom_right2, Sprite.balloom_right3, (int) System.currentTimeMillis() / 100, 20).getFxImage();
                break;
            case "LEFT":
                newX -= speed;
                img = Sprite.movingSprite(Sprite.balloom_left1, Sprite.balloom_left2, Sprite.balloom_left3, (int) System.currentTimeMillis() / 100, 20).getFxImage();
                break;
            case "RIGHT":
                newX += speed;
                img = Sprite.movingSprite(Sprite.balloom_right1, Sprite.balloom_right2, Sprite.balloom_right3, (int) System.currentTimeMillis() / 100, 20).getFxImage();
                break;
        }

        if (!checkCollision(newX, newY)) {
            x = newX;
            y = newY;
            moved = true;
            stuckCounter = 0;
        } else {
            stuckCounter++;
            if (stuckCounter >= MAX_STUCK_FRAMES) {
                setRandomDirection();
                stuckCounter = 0;
            }
        }

        Bomber bomber = findBomber();
        if (bomber != null && checkCollisionWithBomber(newX, newY, bomber)) {
            System.out.println("Balloon caught Bomber! Game Over.");
            // TODO: Thêm logic kết thúc game (ví dụ: dừng timer, hiển thị thông báo)
        }
    }

    private Bomber findBomber() {
        for (Entity entity : game.getEntities()) {
            if (entity instanceof Bomber) {
                return (Bomber) entity;
            }
        }
        return null;
    }

    private boolean checkCollision(int newX, int newY) {
        int scaledSize = Sprite.SCALED_SIZE;
        for (Entity entity : game.getStillObjects()) {
            if (entity instanceof Wall || entity instanceof Brick) {
                if (newX < entity.getX() + scaledSize &&
                        newX + scaledSize > entity.getX() &&
                        newY < entity.getY() + scaledSize &&
                        newY + scaledSize > entity.getY()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkCollisionWithBomber(int newX, int newY, Bomber bomber) {
        int scaledSize = Sprite.SCALED_SIZE;
        return newX < bomber.getX() + scaledSize &&
                newX + scaledSize > bomber.getX() &&
                newY < bomber.getY() + scaledSize &&
                newY + scaledSize > bomber.getY();
    }
}