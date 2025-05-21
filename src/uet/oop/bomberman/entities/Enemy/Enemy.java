package uet.oop.bomberman.entities.Enemy;

import javafx.scene.image.Image;
import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.entities.World.Brick;
import uet.oop.bomberman.entities.World.Entity;
import uet.oop.bomberman.entities.World.Wall;
import uet.oop.bomberman.graphics.Sprite;

public abstract class Enemy extends Entity {
    protected int speed;
    protected GameManager gameManager;
    protected boolean isAlive = true;
    protected boolean isRemoved = false; //Dung de xoa oneal/cac enemy khac
    private int deathTimer = 0;
    private final int DEATH_DELAY = 60; // số frame delay trước khi xóa, tùy chỉnh



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

    public void takeDamage(int amount) {
        die();
    }

    //Logic xu ly giet cac enemy(co the tai su dung)
    public boolean isAlive() {
        return isAlive;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void die() {
        if (isAlive) {
            isAlive = false;
            deathTimer = DEATH_DELAY;
            // KHÔNG đánh dấu xóa ngay
        }
    }

    public boolean isDeath() {
        if (!isAlive) {
            if (deathTimer > 0) {
                deathTimer--;
                return true;  // vẫn đang delay, dừng update tiếp
            } else {
                isRemoved = true;
                gameManager.markForRemoval(this);
                return true;  // đã đánh dấu xóa, không update nữa
            }
        }
        return false; // chưa chết, tiếp tục update bình thường
    }

    //Khong cho enemy di vao o chua bomb hoac entity khac
    //ham kiem tra va cham chung cho tat ca enemy khong di vao cac o co brick wall hoac bomb
    public boolean checkCollision(int newX, int newY) {
        int scaledSize = Sprite.SCALED_SIZE;

        // Kiểm tra va chạm với Wall và Brick
        for (Entity entity : gameManager.getStillObjects()) {
            if (entity instanceof Wall || entity instanceof Brick) {
                if (newX < entity.getX() + scaledSize &&
                        newX + scaledSize > entity.getX() &&
                        newY < entity.getY() + scaledSize &&
                        newY + scaledSize > entity.getY()) {
                    return true;
                }
            }
        }

        // Kiểm tra va chạm với Bomb chưa bị loại bỏ
        for (Entity entity : gameManager.getEntities()) {
            if (entity instanceof Bomb && !((Bomb) entity).isRemoved()) {
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
}