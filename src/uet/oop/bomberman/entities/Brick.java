package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.Item.BombItem;
import uet.oop.bomberman.entities.Item.FlameItem;
import uet.oop.bomberman.entities.Item.Item;
import uet.oop.bomberman.entities.Item.SpeedItem;
import uet.oop.bomberman.graphics.Sprite;

import java.util.Random;


public class Brick extends Entity {
    private boolean destroyed = false;
    private int destroyAnimationCount = 30;
    private BombermanGame game; // Thêm tham chiếu đến game

    public Brick(int x, int y, Image img, BombermanGame game) {
        super(x, y, img);
        this.game = game; // Lưu tham chiếu
    }

    @Override
    public void update() {
        if (destroyed) {
            if (destroyAnimationCount > 0) {
                destroyAnimationCount--;
                // Cập nhật animation phá gạch
                img = Sprite.movingSprite(Sprite.brick_exploded, Sprite.brick_exploded1,
                        Sprite.brick_exploded2, 30 - destroyAnimationCount, 30).getFxImage();
            } else {
                remove();
            }
        }
    }

    public void destroy() {
        if (!destroyed) {
            destroyed = true;
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    private void remove() {
        int gridX = x / Sprite.SCALED_SIZE;
        int gridY = y / Sprite.SCALED_SIZE;

        game.replaceEntity(this, new Grass(gridX, gridY, Sprite.grass.getFxImage()));

        dropItem();
    }

    private void dropItem() {
        Random rand = new Random();
        int chance = rand.nextInt(100); // Xác suất 20%

        if (chance < 90) {
            int ItemX = getX();
            int ItemY = getY();
            int itemType = rand.nextInt(3); // Giả sử có 2 loại item
            Item newItem;

            switch (itemType) {
                case 0:
                    newItem = new BombItem(ItemX, ItemY);
                    break;
                case 1:
                    newItem = new SpeedItem(ItemX, ItemY);
                    break;
                case 2:
                    newItem = new FlameItem(ItemX, ItemY);
                    break;
                default:
                    newItem = null;
                    break;

            }

            if (newItem != null) {
                game.getStillObjects().add(newItem); // Thêm item vào stillObjects
            }
        }
    }
}