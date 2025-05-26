package uet.oop.bomberman.entities.World;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.bomberman.GameEngine.BombermanGame;
import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.Input;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.entities.Bomb.FlameSegments;
import uet.oop.bomberman.entities.Item.Item;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.entities.Bomb.Flame;

import javafx.geometry.Rectangle2D;

import java.util.ArrayList;
import java.util.List;

public class Bomber extends Entity {
    private Input input;
    private double speed = 1.5;
    private double realX, realY;
    private int animate = 0;
    private final int MAX_ANIMATE = 7500;
    private GameManager game;
    private int maxBombs = 1;
    protected int bombPlaced = 0;
    private Bomb pendingBomb;
    private long bombPlaceTime = -1; // nanoTime
    private final long BOMB_APPEAR_DELAY_NS = 200_000_000L; // 200ms
    private boolean hasLeftTile = false;
    private int flameLength = 1;
    private static final double EPSILON = 0.01;
    private boolean isAlive = true;
    private int deathTimer = 60; // Thời gian cho animation chết
    private int deathFrame = 0;  // Để xác định sprite chết nào đang dùng

    //Item Duration
    private double defaultSpeed = 1.5; // Lưu tốc độ mặc định
    private int defaultMaxBombs = 1; // Lưu số bom mặc định
    private int defaultFlameLength = 1; // Lưu độ dài ngọn lửa mặc định
    private static class ItemEffect {
        String type; // "speed", "bomb", "flame"
        long startTime; // Thời điểm nhặt vật phẩm
        long duration; // Thời gian hiệu lực (nanoseconds)
        ItemEffect(String type, long duration) {
            this.type = type;
            this.startTime = System.nanoTime();
            this.duration = duration;
        }
    }

    private List<ItemEffect> activeEffects = new ArrayList<>();
    private static final long ITEM_DURATION_NS = 5_000_000_000L; // 5 giây


    public Bomber(int x, int y, Image img, Input input, GameManager game) {
        super(x, y, img);
        this.input = input;
        this.game = game;
        this.realX = x * Sprite.SCALED_SIZE;
        this.realY = y * Sprite.SCALED_SIZE;
    }

    @Override
    public void update() {
        if (!isAlive) {
            if (deathTimer > 0) {
                deathTimer--;
                if (deathTimer % 10 == 0) {
                    deathFrame++; // đổi sprite mỗi 20 frame
                }
            }
            return;


        }

        animate();
        move();
        this.x = (int) realX;
        this.y = (int) realY;

        if (input.isSpaceJustPressed() && canPlaceBomb()) {
            placeBomb();
        }

        // Xử lý bomb trì hoãn
        if (pendingBomb != null) {
            if (!hasLeftTile && hasLeftBombTile()) {
                hasLeftTile = true;
                bombPlaceTime = System.nanoTime();
            }

            if (hasLeftTile && System.nanoTime() - bombPlaceTime >= BOMB_APPEAR_DELAY_NS) {
                game.getEntities().add(pendingBomb);
                pendingBomb = null;
                bombPlaceTime = -1;
                hasLeftTile = false;
            }
        }

        updateItemEffects();

        checkItemCollision();
    }

    public void render(GraphicsContext gc) {
        if (!isAlive) {
            Image deadSprite;
            switch (deathFrame) {
                case 0:
                    deadSprite = Sprite.player_dead1.getFxImage(); break;
                case 1:
                    deadSprite = Sprite.player_dead2.getFxImage(); break;
                case 2:
                default:
                    deadSprite = Sprite.player_dead3.getFxImage(); break;
            }
            gc.drawImage(deadSprite, x, y);
            return;
        }

        // Bomber đang sống
        gc.drawImage(img , x, y);
    }

    private boolean hasLeftBombTile() {
        int bomberUnitX = (int) Math.floor((realX + Sprite.SCALED_SIZE / 2) / Sprite.SCALED_SIZE);
        int bomberUnitY = (int) Math.floor((realY + Sprite.SCALED_SIZE / 2) / Sprite.SCALED_SIZE);
        int bombUnitX = pendingBomb.getX() / Sprite.SCALED_SIZE;
        int bombUnitY = pendingBomb.getY() / Sprite.SCALED_SIZE;
        return bomberUnitX != bombUnitX || bomberUnitY != bombUnitY;
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
        int buffer = 6;

        double left = newX + buffer;
        double right = newX + size - buffer - 1;
        double top = newY + buffer;
        double bottom = newY + size - buffer - 1;

        int[][] checkUnit = {
                {(int) (left / size), (int) (top / size)},
                {(int) (right / size), (int) (top / size)},
                {(int) (left / size), (int) (bottom / size)},
                {(int) (right / size), (int) (bottom / size)},
        };

        for (int[] unit : checkUnit) {
            int checkX = unit[0];
            int checkY = unit[1];

            if (checkX < 0 || checkX >= BombermanGame.WIDTH || checkY < 0 || checkY >= BombermanGame.HEIGHT) {
                return false;
            }

            for (Entity entity : game.getStillObjects()) {
                int entityX = entity.getX() / size;
                int entityY = entity.getY() / size;

                if (entityX == checkX && entityY == checkY &&
                        (entity instanceof Wall || entity instanceof Brick)) {

                    double entityLeft = entity.getX();
                    double entityRight = entity.getX() + size - 1;
                    double entityTop = entity.getY();
                    double entityBottom = entity.getY() + size - 1;

                    if (!(right < entityLeft || left > entityRight || bottom < entityTop || top > entityBottom)) {
                        return false;
                    }
                }
            }

            for (Entity entity : game.getEntities()) {
                if (entity instanceof Bomb && !((Bomb) entity).isRemoved()) {
                    int bombX = entity.getX() / size;
                    int bombY = entity.getY() / size;
                    if (bombX == checkX && bombY == checkY) {
                        double bombLeft = entity.getX();
                        double bombRight = entity.getX() + size - 1;
                        double bombTop = entity.getY();
                        double bombBottom = entity.getY() + size - 1;

                        if (!(right < bombLeft || left > bombRight || bottom < bombTop || top > bombBottom)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean canPlaceBomb() {
        return bombPlaced < maxBombs && pendingBomb == null;
    }

    public void placeBomb() {
        if (canPlaceBomb()) {
            int bombX = (int) Math.floor((realX + Sprite.SCALED_SIZE / 2) / Sprite.SCALED_SIZE);
            int bombY = (int) Math.floor((realY + Sprite.SCALED_SIZE / 2) / Sprite.SCALED_SIZE);
            pendingBomb = new Bomb(bombX, bombY, flameLength, game, this);
            bombPlaced++;
            hasLeftTile = false;
            bombPlaceTime = -1;
        }
    }

    public void bombExploded() {
        if (bombPlaced > 0) {
            bombPlaced--;
        }
        if (pendingBomb != null && pendingBomb.isRemoved()) {
            pendingBomb = null;
            bombPlaceTime = -1;
            hasLeftTile = false;
        }
    }

    private void checkItemCollision() {
        Rectangle2D bounds = this.getBoundary();

        for(Entity entity : game.getStillObjects()) {
            if (entity instanceof Item) {
                Rectangle2D itemBounds = ((Item) entity).getBoundary();

                if (bounds.intersects(itemBounds)) {
                    Item powerUp = (Item) entity;
                    powerUp.applyEffect(this); // áp dụng hiệu ứng (ví dụ tăng bomb)
                    game.getStillObjects().remove(entity);
                    break;
                }
            }
        }
    }

    public void increaseBombPlace() {
        if(maxBombs < 2) {
            maxBombs += 1;
            activeEffects.add(new ItemEffect("bomb", ITEM_DURATION_NS));
        } else {
            // Đã max, reset thời gian nếu hiệu ứng tồn tại
            for (ItemEffect effect : activeEffects) {
                if (effect.type.equals("bomb")) {
                    effect.startTime = System.nanoTime(); // Reset thời gian
                    break;
                }
            }
        }
    }

    public void increaseFlameLength() {
        if(flameLength < 2) {
            flameLength += 1;
            activeEffects.add(new ItemEffect("flame", ITEM_DURATION_NS));
        } else {
            // Đã max, reset thời gian nếu hiệu ứng tồn tại
            for (ItemEffect effect : activeEffects) {
                if (effect.type.equals("flame")) {
                    effect.startTime = System.nanoTime(); // Reset thời gian
                    break;
                }
            }
        }
    }

    public void increaseSpeed() {
        if(speed <= 2.5) {
            speed += 0.5;
            activeEffects.add(new ItemEffect("speed", ITEM_DURATION_NS));
        } else {
            // Đã max, reset thời gian nếu hiệu ứng tồn tại
            for (ItemEffect effect : activeEffects) {
                if (effect.type.equals("speed")) {
                    effect.startTime = System.nanoTime(); // Reset thời gian
                    break;
                }
            }
        }
    }

    private void animate() {
        if (animate < MAX_ANIMATE) animate++;
        else animate = 0;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public Bomber getBomber() {
        return this;
    }

    public void kill() {
        isAlive = false;
        deathTimer = 60;  // tổng thời gian chết
        deathFrame = 0;   // bắt đầu từ sprite đầu tiên
    }

    public boolean isDeathAnimationFinished() {
        return !isAlive && deathTimer <= 0;
    }

    private void updateItemEffects() {
        long currentTime = System.nanoTime();
        List<ItemEffect> expired = new ArrayList<>();

        for (ItemEffect effect : activeEffects) {
            if (currentTime - effect.startTime >= effect.duration) {
                // Hết thời gian, hoàn tác hiệu ứng
                switch (effect.type) {
                    case "speed":
                        speed = defaultSpeed;
                        break;
                    case "bomb":
                        maxBombs = defaultMaxBombs;
                        break;
                    case "flame":
                        flameLength = defaultFlameLength;
                        break;
                }
                expired.add(effect);
            }
        }
        activeEffects.removeAll(expired);
    }
}
