package uet.oop.bomberman.entities.Bomb;

import javafx.scene.canvas.GraphicsContext;
import uet.oop.bomberman.GameEngine.BombermanGame;
import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.entities.World.Brick;
import uet.oop.bomberman.entities.World.Entity;
import uet.oop.bomberman.entities.World.Wall;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;


public class Flame extends Entity {
    private int direction;
    private int length;
    private List<FlameSegments> flameSegments = new ArrayList<>();
    private int gridX, gridY;
    private int timeToLive = 30;
    private boolean isRemoved = false;
    private static final int FLAME_LIFETIME = 30;
    private GameManager game;

    public Flame(int xUnit, int yUnit, int direction, int length, GameManager game) {
        super(xUnit, yUnit, null);
        this.gridX = xUnit;
        this.gridY = yUnit;
        this.direction = direction;
        this.length = length;
        this.game = game;

        createFlameSegment();
    }

    private void createFlameSegment() {
        //Flame center
        flameSegments.add(new FlameSegments(gridX, gridY, -1, false, FLAME_LIFETIME));

        for(int i = 0; i < length; i++){
            boolean isLast = i == (length - 1);
            int dx = gridX;
            int dy = gridY;

            switch (direction) {
                case 0: dy -= i + 1; break; //Up
                case 1: dy += i + 1; break; //Down
                case 2: dx += i + 1; break; //Right
                case 3: dx -= i + 1; break; //Left
            }

            for (Entity entity : game.getStillObjects()) {
                int ex = entity.getX() / Sprite.SCALED_SIZE;
                int ey = entity.getY() / Sprite.SCALED_SIZE;

                if (ex == dx && ey == dy) {
                    if (entity instanceof Wall) {
                        return; // Ngừng lan rộng nếu gặp wall
                    }
                    if (entity instanceof Brick) {
                        ((Brick) entity).destroy(); // Phá brick
                        return; // Ngừng lan rộng sau khi phá brick
                    }
                }
            }

//            System.out.println("Tạo flame segment tại (" + dx + ", " + dy + ")" + ", isLast = " + isLast);

            flameSegments.add(new FlameSegments(dx , dy , direction, isLast, FLAME_LIFETIME));
        }
    }

    public List<FlameSegments> getFlameSegments() {
        return flameSegments;
    }

    @Override
    public void update() {
        // Cập nhật các flame segment và xóa các segment đã hết thời gian sống
        for (int i = flameSegments.size() - 1; i >= 0; i--) {
            FlameSegments segment = flameSegments.get(i);
            segment.update();
            if (segment.isExpired()) {
                flameSegments.remove(i);  // Xóa segment nếu hết thời gian sống
            }
        }
    }

    public boolean isRemoved() {
        return isRemoved;
    }
        @Override
    public void render(GraphicsContext gc) {
        for (FlameSegments seg : flameSegments) {
            seg.render(gc);
        }
    }
}
