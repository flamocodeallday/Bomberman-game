package uet.oop.bomberman.entities.Item;

import javafx.scene.canvas.GraphicsContext;
import uet.oop.bomberman.GameEngine.BombermanGame;
import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.entities.World.Bomber;
import uet.oop.bomberman.entities.World.Entity;
import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

public abstract class Item extends Entity {
    protected boolean isActive = true;
    private static final int TIME_LIMIT = 5000; // Thời gian tồn tại của item (5 giây)
    private long startTime;  // Thời điểm item được nhặt
    private GameManager game;

    public Item(int x, int y, Image image) {
        super(x / Sprite.SCALED_SIZE, y / Sprite.SCALED_SIZE, image);
        this.startTime = System.nanoTime(); // Lưu lại thời gian nhặt item
    }

    public boolean   isActive() {
        return isActive;
    }

    public void collect() {
        isActive = false;
    }

    @Override
    public void update() {
        long elapsedTime = System.nanoTime() - startTime;
        if (elapsedTime > TIME_LIMIT * 1000000L) {
            game.markForRemoval(this); // Đánh dấu để xóa
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        // Vẽ item tại vị trí (x, y)
        gc.drawImage(img, getX(), getY());
    }

    public abstract void applyEffect(Bomber bomber); // Bomberman sẽ gọi khi đụng vào item

}
