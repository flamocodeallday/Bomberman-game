package uet.oop.bomberman.entities.Bomb;


import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.entities.Enemy.Enemy;
import uet.oop.bomberman.entities.World.Entity;
import uet.oop.bomberman.graphics.Sprite;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;


public class FlameSegments extends Entity {
    private int direction;
    private boolean isLast;
    private int animate = 0;
    private final int MAX_ANIMATE = 30;
    private int lifeTime;
    private GameManager game;


    public FlameSegments(int dx, int dy, int direction, boolean isLast, int lifeTime, GameManager game) {
        super(dx, dy, null);
        this.direction = direction;
        this.isLast = isLast;
        this.lifeTime = lifeTime;
        this.img = getFlameSprite();
        this.game = game;
    }

    @Override
    public void update() {
        animate++;
        if (animate >= MAX_ANIMATE) {
            animate = 0;
        }
        img = getFlameSprite();

        // Xử lý va chạm với enemy
        for (Entity e : game.getEntities()) {
            if (e instanceof Enemy) {
                Enemy enemy = (Enemy) e;
                if (enemy.isAlive() && this.intersects(enemy)) {
                    enemy.die();
                }
            }
        }

        // Giảm thời gian sống và xóa nếu hết thời gian
        if (lifeTime > 0) {
            lifeTime--;
        }
    }

    // Kiểm tra nếu FlameSegment đã hết thời gian sống
    public boolean isExpired() {
        return lifeTime <= 0;
    }

    private Image getFlameSprite() {
        int frame = (animate / 10) % 3;

        switch (direction) {
            case 0: // Up
                return isLast ? switchFrame(Sprite.explosion_vertical_top_last, Sprite.explosion_vertical_top_last1,
                        Sprite.explosion_vertical_top_last2, frame)
                        : switchFrame(Sprite.explosion_vertical, Sprite.explosion_vertical1,
                        Sprite.explosion_vertical2, frame);
            case 1: // Down
                return isLast ? switchFrame(Sprite.explosion_vertical_down_last,
                        Sprite.explosion_vertical_down_last1, Sprite.explosion_vertical_down_last2, frame)
                        : switchFrame(Sprite.explosion_vertical, Sprite.explosion_vertical1,
                        Sprite.explosion_vertical2, frame);
            case 2: // Right
                return isLast ? switchFrame(Sprite.explosion_horizontal_right_last,
                        Sprite.explosion_horizontal_right_last1, Sprite.explosion_horizontal_right_last2, frame)
                        : switchFrame(Sprite.explosion_horizontal,
                        Sprite.explosion_horizontal1, Sprite.explosion_horizontal2, frame);
            case 3: // Left
                return isLast ? switchFrame(Sprite.explosion_horizontal_left_last, Sprite.explosion_horizontal_left_last1,
                        Sprite.explosion_horizontal_left_last2, frame)
                        : switchFrame(Sprite.explosion_horizontal,
                        Sprite.explosion_horizontal1, Sprite.explosion_horizontal2, frame);
            default: // Center (nếu cần)
                return switchFrame(Sprite.bomb_exploded, Sprite.bomb_exploded1, Sprite.bomb_exploded2, frame);
        }
    }

    private Image switchFrame(Sprite s1, Sprite s2, Sprite s3, int frame) {
        switch (frame) {
            case 0: return s1.getFxImage();
            case 1: return s2.getFxImage();
            case 2: return s3.getFxImage();
            default: return s1.getFxImage();
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(img, x, y);
    }
}
