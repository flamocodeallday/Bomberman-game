package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

public class Brick extends Entity {
    private boolean destroyed = false;      // Gạch có bị phá chưa?
    private int destroyAnimationCount = 30; // Thời gian chạy animation

    public Brick(int x, int y, Image img) {
        super(x, y, img);
    }

    @Override
    public void update() {
        if (destroyed) {
            if (destroyAnimationCount > 0) {
                destroyAnimationCount--;
                // Cập nhật hình ảnh animation nổ nếu muốn
                // Ví dụ: thay đổi sprite theo từng frame của animation
            } else {
                // Sau animation thì biến mất khỏi game
                remove(); // Loại bỏ Brick khỏi danh sách các Entity trong game
            }
        }
    }

    public void destroy() {
        destroyed = true;
        // Đặt hình ảnh hiệu ứng nổ, ví dụ:
        this.img = Sprite.brick_exploded.getFxImage();
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    // Giả sử remove sẽ loại bỏ Brick khỏi danh sách các Entity trong game
    private void remove() {
    }
}
