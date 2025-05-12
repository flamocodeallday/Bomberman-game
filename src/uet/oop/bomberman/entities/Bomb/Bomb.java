package uet.oop.bomberman.entities.Bomb;

import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.entities.Bomber;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;


public class Bomb extends Entity {
    private int countDown = 90;
    private int postExplosion= 30;
    private boolean exploded = false;
    private int power;
    private boolean isRemoved = false; // Thêm thuộc tính để đánh dấu xóa

    private BombermanGame game;
    private int animate = 0;

    public Bomb(int xUnit, int yUnit, int power, BombermanGame game) {
        super(xUnit, yUnit, Sprite.bomb.getFxImage());
        this.game = game;
        this.power = power;
    }

    @Override
    public void update() {
        if(!exploded) {
            countDown--;
            if(countDown <= 0) {
                explode();
            } else {
                animate();
                img = Sprite.movingSprite(Sprite.bomb, Sprite.bomb_1, Sprite.bomb_2, animate, 60).getFxImage();

            }
        } else {
            postExplosion--;
            if(postExplosion <= 0) {
                isRemoved = true;
            }
        }
    }

    public void explode() {
        exploded = true;
        img = Sprite.bomb_exploded.getFxImage();

        int gridX = x / Sprite.SCALED_SIZE;
        int gridY = y / Sprite.SCALED_SIZE;

        // Tạo tia lửa cho 4 hướng
        Flame up = new Flame(gridX, gridY, 0, power, game);
        Flame down = new Flame(gridX, gridY, 1, power, game);
        Flame right = new Flame(gridX, gridY, 2, power, game);
        Flame left = new Flame(gridX, gridY, 3, power, game);

        // Thêm tia lửa vào danh sách entity
        game.getEntities().add(up);
        game.getEntities().add(down);
        game.getEntities().add(right);
        game.getEntities().add(left);

        for (Entity entity : game.getEntities()) {
            if (entity instanceof Bomber) {
                ((Bomber) entity).bombExploded();
            }
        }
    }

    private void animate() {
        animate++;
        if (animate > 60) {
            animate = 0;
        }
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(img, x, y); // Vẽ bom tại vị trí x, y
    }
}
