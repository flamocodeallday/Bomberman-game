package uet.oop.bomberman.entities.Enemy;

import javafx.scene.image.Image;
import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.entities.World.Bomber;
import uet.oop.bomberman.entities.World.Brick;
import uet.oop.bomberman.entities.World.Entity;
import uet.oop.bomberman.entities.World.Wall;
import uet.oop.bomberman.graphics.Sprite;

import java.util.Random;

public class Oneal extends Enemy {
    private final int speed = 1; // Tăng tốc độ để di chuyển mượt hơn
    private String direction; // Hướng di chuyển
    private final Random random = new Random();
    private String mode = "RANDOM"; // Chế độ: "RANDOM" hoặc "CHASE"
    private static final int CHASE_DISTANCE = 2 * Sprite.SCALED_SIZE; // Ngưỡng khoảng cách để đuổi
    private int stuckCounter = 0; // Đếm số frame bị kẹt
    private static final int MAX_STUCK_FRAMES = 60; // Đổi hướng sau 60 frame nếu bị kẹt
    private GameManager game;

    public Oneal(int x, int y, Image img, GameManager game) {
        super(x, y, img, game);
        this.game = game;
        setRandomDirection();
    }

    private void setRandomDirection() {
        String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
        direction = directions[random.nextInt(directions.length)];
    }

    @Override
    public void update() {
        Bomber bomber = findBomber();
        if (bomber == null) {
            mode = "RANDOM";
        } else {
            double distance = Math.sqrt(Math.pow(bomber.getX() - x, 2) + Math.pow(bomber.getY() - y, 2));
            if (distance < CHASE_DISTANCE) {
                mode = "CHASE";
            } else {
                mode = "RANDOM";
            }
        }

        if (isDeath()) {
            img = Sprite.oneal_dead.getFxImage();
            return;
        }

        int newX = x;
        int newY = y;
        boolean moved = false;

        if (mode.equals("RANDOM")) {
            switch (direction) {
                case "UP":
                    newY -= speed;
                    img = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_left2, Sprite.oneal_left3, (int) System.currentTimeMillis() / 100, 20).getFxImage();
                    break;
                case "DOWN":
                    newY += speed;
                    img = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3, (int) System.currentTimeMillis() / 100, 20).getFxImage();
                    break;
                case "LEFT":
                    newX -= speed;
                    img = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_left2, Sprite.oneal_left3, (int) System.currentTimeMillis() / 100, 20).getFxImage();
                    break;
                case "RIGHT":
                    newX += speed;
                    img = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3, (int) System.currentTimeMillis() / 100, 20).getFxImage();
                    break;
            }

            if (!checkCollision(newX, newY)) {
                x = newX;
                y = newY;
                moved = true;
                stuckCounter = 0; // Reset counter khi di chuyển thành công
            } else {
                stuckCounter++;
                if (stuckCounter >= MAX_STUCK_FRAMES) {
                    setRandomDirection();
                    stuckCounter = 0;
                }
            }
        } else if (mode.equals("CHASE") && bomber != null) {
            // Thử tất cả các hướng và chọn hướng tốt nhất
            String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
            double bestDistance = Double.MAX_VALUE;
            String bestDirection = direction;
            int bestX = x;
            int bestY = y;

            for (String dir : directions) {
                int tempX = x;
                int tempY = y;
                switch (dir) {
                    case "UP":
                        tempY -= speed;
                        break;
                    case "DOWN":
                        tempY += speed;
                        break;
                    case "LEFT":
                        tempX -= speed;
                        break;
                    case "RIGHT":
                        tempX += speed;
                        break;
                }

                if (!checkCollision(tempX, tempY)) {
                    double distanceToBomber = Math.sqrt(Math.pow(bomber.getX() - tempX, 2) + Math.pow(bomber.getY() - tempY, 2));
                    if (distanceToBomber < bestDistance) {
                        bestDistance = distanceToBomber;
                        bestDirection = dir;
                        bestX = tempX;
                        bestY = tempY;
                    }
                }
            }

            // Cập nhật hướng và vị trí
            direction = bestDirection;
            newX = bestX;
            newY = bestY;

            // Cập nhật sprite dựa trên hướng
            switch (direction) {
                case "UP":
                    img = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_left2, Sprite.oneal_left3, (int) System.currentTimeMillis() / 100, 20).getFxImage();
                    break;
                case "DOWN":
                    img = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3, (int) System.currentTimeMillis() / 100, 20).getFxImage();
                    break;
                case "LEFT":
                    img = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_left2, Sprite.oneal_left3, (int) System.currentTimeMillis() / 100, 20).getFxImage();
                    break;
                case "RIGHT":
                    img = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3, (int) System.currentTimeMillis() / 100, 20).getFxImage();
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

            // Kiểm tra va chạm với Bomber
            if (checkCollisionWithBomber(newX, newY, bomber)) {
                System.out.println("Oneal caught Bomber! Game Over.");
                // TODO: Thêm logic kết thúc game
            }
        }

        // Nếu không di chuyển được, thử hướng ngẫu nhiên
        if (!moved) {
            stuckCounter++;
            if (stuckCounter >= MAX_STUCK_FRAMES) {
                setRandomDirection();
                stuckCounter = 0;
            }
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

//    private boolean checkCollision(int newX, int newY) {
//        int scaledSize = Sprite.SCALED_SIZE;
//
//        // Kiểm tra va chạm với Wall và Brick
//        for (Entity entity : game.getStillObjects()) {
//            if (entity instanceof Wall || entity instanceof Brick) {
//                if (newX < entity.getX() + scaledSize &&
//                        newX + scaledSize > entity.getX() &&
//                        newY < entity.getY() + scaledSize &&
//                        newY + scaledSize > entity.getY()) {
//                    return true;
//                }
//            }
//        }
//
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