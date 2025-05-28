package uet.oop.bomberman.entities.World;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

import javafx.geometry.Rectangle2D;

public abstract class Entity {
    //Tọa độ X tính từ góc trái trên trong Canvas
    protected int x;

    //Tọa độ Y tính từ góc trái trên trong Canvas
    protected int y;
    protected boolean isRemoved = false;

    protected Image img;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    //Khởi tạo đối tượng, chuyển từ tọa độ đơn vị sang tọa độ trong canvas
    public Entity( int xUnit, int yUnit, Image img) {
        this.x = xUnit * Sprite.SCALED_SIZE;
        this.y = yUnit * Sprite.SCALED_SIZE;
        this.img = img;
    }

    public Rectangle2D getBoundary() {
        final int offset = 6; // Điều chỉnh: giảm hitbox mỗi chiều 6 pixel (tùy chỉnh được)
        return new Rectangle2D(
                x + offset / 2.0,
                y + offset / 2.0,
                Sprite.SCALED_SIZE - offset,
                Sprite.SCALED_SIZE - offset
        );
    }


    //Kiem tra va cham voi enemy
    public boolean intersects(Entity other) {
        // Tính tâm đối tượng
        int thisTileX = (int)((this.x + Sprite.SCALED_SIZE / 2) / Sprite.SCALED_SIZE);
        int thisTileY = (int)((this.y + Sprite.SCALED_SIZE / 2) / Sprite.SCALED_SIZE);

        int otherTileX = (int)((other.x + Sprite.SCALED_SIZE / 2) / Sprite.SCALED_SIZE);
        int otherTileY = (int)((other.y + Sprite.SCALED_SIZE / 2) / Sprite.SCALED_SIZE);

        return (thisTileX == otherTileX) && (thisTileY == otherTileY);
    }

    public boolean isRemoved() { return isRemoved; }


    public void render(GraphicsContext gc) {
        gc.drawImage(img, x, y);
    }
    public abstract void update();
}
