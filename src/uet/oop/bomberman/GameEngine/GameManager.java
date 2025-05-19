package uet.oop.bomberman.GameEngine;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import uet.oop.bomberman.Input;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.entities.Bomb.Flame;
import uet.oop.bomberman.entities.Enemy.Balloon;
import uet.oop.bomberman.entities.Enemy.Oneal;
import uet.oop.bomberman.entities.World.*;
import uet.oop.bomberman.graphics.Sprite;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static uet.oop.bomberman.GameEngine.BombermanGame.HEIGHT;
import static uet.oop.bomberman.GameEngine.BombermanGame.WIDTH;

public class GameManager {
    private Input input;
    private GraphicsContext gc;
    private Canvas canvas;
    private List<Entity> entities = new ArrayList<>();
    private List<Entity> stillObjects = new ArrayList<>();

    private List<Bomb> bombsAdd = new ArrayList<>(); //List trung gian
    private List<Entity> toRemove = new ArrayList<>();

    private Bomber bomberman;
    private boolean isGameOver = false;


    //Thêm bomb qua list trung gian để tránh lỗi
    public void addNewBomb(Bomb newBomb) {
        bombsAdd.add(newBomb);
    }

    //List de xoa
    public void markForRemoval(Entity entity) {
        toRemove.add(entity);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Entity> getStillObjects() {
        return stillObjects;
    }

    public GameManager(Input input) {
        this.input = input;
    }

    /** Khoi tao game. */
    public void initialize(Group root, Stage stage) {
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        canvas.requestFocus();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                render();
                update();
            }
        };

        timer.start();
        createMap();
        bomberman = new Bomber(1, 1, Sprite.player_right.getFxImage(), input, this);
        entities.add(bomberman);
    }

    public void createMap() {
        try {
            // Adjust the file path to match your system
            BufferedReader reader = new BufferedReader(new FileReader("D:\\Code\\Bomberman-game\\res\\levels\\Level1.txt"));
            String line;
            int j = 0;

            while ((line = reader.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);

                    // Luôn thêm nền Grass trước
                    stillObjects.add(new Grass(i, j, Sprite.grass.getFxImage()));

                    switch (c) {
                        case '#':
                            stillObjects.add(new Wall(i, j, Sprite.wall.getFxImage()));
                            break;
                        case '*':
                            stillObjects.add(new Brick(i, j, Sprite.brick.getFxImage(), this));
                            break;
                        case '1':
                            entities.add(new Balloon(i, j, Sprite.balloom_left1.getFxImage(), this));
                            break;
                        // Uncomment if you want to add Oneal enemies
                        // case '2':
                        //     entities.add(new Oneal(i, j, Sprite.oneal_left1.getFxImage(), this));
                        //     break;
                        // Các ký tự khác chỉ cần Grass (đã được thêm ở trên)
                    }
                }
                j++;
            }
            reader.close(); // Đóng tệp sau khi đọc xong

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        List<Entity> stillObjectsCopy = new ArrayList<>(stillObjects);
        for (Entity obj : stillObjectsCopy) {
            obj.update();
        }
        stillObjects.removeAll(toRemove);
        toRemove.clear();

        for (Entity obj : stillObjects) {
            obj.update();
        }
        // Tạo bản sao của entities để lặp
        List<Entity> entitiesToUpdate = new ArrayList<>(entities);
        // Tạo danh sách tạm cho các thực thể cần thêm
        List<Entity> entitiesToAdd = new ArrayList<>(bombsAdd);
        bombsAdd.clear();

        // Cập nhật tất cả thực thể
        for (Entity entity : entitiesToUpdate) {
            entity.update();
        }

        // Thêm các thực thể mới
        entities.addAll(entitiesToAdd);

        // Kiểm tra va chạm flame với bomber
        if (bomberman.isAlive()) {
            for (Entity obj : stillObjects) {
                if (obj instanceof Flame flame) {
                    flame.checkCollisionWithBomber(bomberman);
                }
            }
        } else {
            if (bomberman.isDeathAnimationFinished()) {
                isGameOver = true;
            }
        }

        // Xóa các bom đã đánh dấu isRemoved
        entities.removeIf(entity -> entity instanceof Bomb && ((Bomb) entity).isRemoved());
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        stillObjects.forEach(g -> g.render(gc));
        entities.forEach(g -> g.render(gc));
    }

    //Xu ly enemy chet hoac pha brick tao ra portal va itemddd
    public void replaceEntity(Entity oldEntity, Entity newEntity) {
        int index = stillObjects.indexOf(oldEntity);
        if (index != -1) {
            stillObjects.set(index, newEntity);
        }
    }
}