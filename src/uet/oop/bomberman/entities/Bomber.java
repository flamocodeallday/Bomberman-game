package uet.oop.bomberman.entities;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.Input;
import uet.oop.bomberman.graphics.Sprite;

import java.util.List;

public class Bomber extends Entity {
    private Input input;
    private double speed = 1.0;
    private double realX, realY;
    private int animate = 0;
    private final int MAX_ANIMATE = 7500;
    private BombermanGame game;

    public Bomber(int x, int y, Image img, Input input, BombermanGame game) {
        super(x, y, img);
        this.input = input;
        this.game = game;
        this.realX = x * Sprite.SCALED_SIZE;
        this.realY = y * Sprite.SCALED_SIZE;
    }

    @Override
    public void update() {
        animate();
        move();
        // Cập nhật lại toạ độ int để render
        this.x = (int) realX;
        this.y = (int) realY;
    }

    private void move() {
        double dx = 0, dy = 0;
        if (input.isUp()) {
            dy -= speed;
            img = Sprite.movingSprite(Sprite.player_up, Sprite.player_up_1, Sprite.player_up_2, animate, 20).getFxImage();
        }
        if (input.isDown()) {
            dy += speed;
            img = Sprite.movingSprite(Sprite.player_down, Sprite.player_down_1, Sprite.player_down_2, animate, 20).getFxImage();
        }
        if (input.isLeft()) {
            dx -= speed;
            img = Sprite.movingSprite(Sprite.player_left, Sprite.player_left_1, Sprite.player_left_2, animate, 20).getFxImage();
        }
        if (input.isRight()) {
            dx += speed;
            img = Sprite.movingSprite(Sprite.player_right, Sprite.player_right_1, Sprite.player_right_2, animate, 20).getFxImage();
        }

        if (canMove(realX + dx, realY + dy)) {
            realX += dx;
            realY += dy;
        }
    }

    public boolean canMove(double newX, double newY) {
        int size = Sprite.SCALED_SIZE;

        // Tạo buffer nhỏ để thu nhỏ hitbox, giúp tránh kẹt khi đi gần tường
        int buffer = 6;  // có thể điều chỉnh

        double left = newX + buffer;
        double right = newX + size - buffer - 1;
        double top = newY + buffer;
        double bottom = newY + size - buffer - 1;

        for (Entity entity : game.getStillObjects()) {
            if (entity instanceof Wall) {
                int wallX = entity.getX();
                int wallY = entity.getY();

                // Tọa độ góc của wall
                double wallLeft = wallX;
                double wallRight = wallX + size - 1;
                double wallTop = wallY;
                double wallBottom = wallY + size - 1;

                // Kiểm tra va chạm hình chữ nhật
                if (!(right < wallLeft || left > wallRight || bottom < wallTop || top > wallBottom)) {
                    return false;  // Có va chạm với Wall
                }
            }
        }

        return true;  // Không có va chạm
    }

    private void animate() {
        if (animate < MAX_ANIMATE) animate++;
        else animate = 0;
    }
}

