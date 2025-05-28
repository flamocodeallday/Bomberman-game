package uet.oop.bomberman.entities.Bomb;

import javafx.scene.canvas.GraphicsContext;
import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.entities.World.Bomber;
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
    private boolean isRemoved = false;
    private static final int FLAME_LIFETIME = 30;
    private GameManager game;
    private Bomber bomber;

    public Flame(int xUnit, int yUnit, int direction, int length, GameManager game, Bomber bomber) {
        super(xUnit, yUnit, null);
        this.gridX = xUnit;
        this.gridY = yUnit;
        this.direction = direction;
        this.length = length;
        this.game = game;
        this.bomber = bomber;

        createFlameSegment();
    }

    private void createFlameSegment() {
        //Flame center
        flameSegments.add(new FlameSegments(gridX, gridY, -1, false, FLAME_LIFETIME, game));

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

            flameSegments.add(new FlameSegments(dx , dy , direction, isLast, FLAME_LIFETIME, game));
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
        checkCollisionWithBomber(bomber);
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

    public void checkCollisionWithBomber(Bomber bomber) {
        if (!bomber.isAlive()) return;

        int bx = bomber.getX() / Sprite.SCALED_SIZE;
        int by = bomber.getY() / Sprite.SCALED_SIZE;

        for (FlameSegments segment : flameSegments) {
            int fx = segment.getX() / Sprite.SCALED_SIZE;
            int fy = segment.getY() / Sprite.SCALED_SIZE;

            if (bx == fx && by == fy) {

                bomber.kill();
                break;
            }
        }
    }
}
