package uet.oop.bomberman.GameEngine;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import uet.oop.bomberman.Input;
import uet.oop.bomberman.UI.UIManager;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.entities.Bomb.Flame;
import uet.oop.bomberman.entities.Enemy.Balloon;
import uet.oop.bomberman.entities.Enemy.Enemy;
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


    //TEST
    private UIManager ui;
    private Group root;
    private Stage stage;
    private AnimationTimer timer;
    public void setUI(UIManager ui, Group root, Stage stage) {
        this.ui = ui;
        this.root = root;
        this.stage = stage;
    }


    private int currentStage;
    private static final String[] levels = {"res/levels/Level1.txt", "res/levels/Level2.txt", "res/levels/Level3.txt"};


    public AnimationTimer getTimer() {
        return timer;
    }
    public int getCurrentStage() {
        return currentStage;
    }
    public UIManager getUI() {
        return ui;
    }
    //TEST

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

        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                render();
                update();
            }
        };
        currentStage = 0;
        timer.start();

        createMap(currentStage);
        bomberman = new Bomber(1, 1, Sprite.player_right.getFxImage(), input, this);
        entities.add(bomberman);
    }

    public void restart(Group root, int currentStage) {
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        canvas.requestFocus();

        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                render();
                update();
            }
        };
        timer.start();

        createMap(currentStage);
        bomberman = new Bomber(1, 1, Sprite.player_right.getFxImage(), input, this);
        entities.add(bomberman);
    }

    public void respawn(Group root, Stage stage) {
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        canvas.requestFocus();

        timer = new AnimationTimer() {
            @Override

            public void handle(long l) {
                render();
                update();
            }
        };

        timer.start();

        bomberman = new Bomber(1, 1, Sprite.player_right.getFxImage(), input, this);
        entities.add(bomberman);
    }

    public void nextLevel(Group root, int currentStage) {
        root.getChildren().clear();
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        canvas.requestFocus();

        timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                render();
                update();
            }
        };
        timer.start();
        createMap(currentStage);
        bomberman = new Bomber(1, 1, Sprite.player_right.getFxImage(), input, this);
        entities.add(bomberman);
    }

    public void createMap(int currentStage) {
        try {
            // Adjust the file path to match your system
            BufferedReader reader = new BufferedReader(new FileReader(levels[currentStage]));
            String line;
            int j = 0;
            if (!stillObjects.isEmpty()) {
                stillObjects.clear();
            }
            if (!entities.isEmpty()) {
                entities.clear();
            }
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
                            entities.add(new Balloon(i, j, Sprite.balloon_left1.getFxImage(), this));
                            break;
                        // Uncomment if you want to add Oneal enemies
                        case '2':
                            entities.add(new Oneal(i, j, Sprite.oneal_left1.getFxImage(), this));
                            break;
                        case 'g':
                            stillObjects.add(new Portal(i, j, Sprite.portal.getFxImage()));
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

    public boolean isWincurrentStage() {
        for (Entity entity : entities) {
            if (entity instanceof Enemy) {
                return false;
            }
        }
        return true;
    }

    public Portal getPortal(List<Entity> stillObjects) {
        for (Entity entity : stillObjects) {
            if (entity instanceof Portal) {
                return (Portal) entity;
            }
        }
        return null;
    }

    public void update() {
        // 1. Cập nhật tất cả stillObjects (tạo bản sao để tránh ConcurrentModification)

        List<Entity> stillObjectsCopy = new ArrayList<>(stillObjects);
        for (Entity obj : stillObjectsCopy) {
            obj.update();
        }

        // 2. Xóa các stillObjects đã được đánh dấu trong toRemove, sau đó clear danh sách toRemove
        stillObjects.removeAll(toRemove);
        toRemove.clear();

        // 3. Cập nhật tất cả entities (bản sao)
        List<Entity> entitiesToUpdate = new ArrayList<>(entities);
        for (Entity entity : entitiesToUpdate) {
            entity.update();
        }

        // 4. Thêm các bomb mới được thêm vào
        if (!bombsAdd.isEmpty()) {
            entities.addAll(bombsAdd);
            bombsAdd.clear();
        }

        // 5. Kiểm tra va chạm flame với bomber
        if (bomberman.isAlive()) {
            for (Entity obj : stillObjects) {
                if (obj instanceof Flame flame) {
                    flame.checkCollisionWithBomber(bomberman);
                }
            }
        } else {
            if (bomberman.isDeathAnimationFinished()) {

                //TODO
                if (timer != null) {
                    timer.stop();  // Dừng vòng lặp game
                }

                Platform.runLater(() -> {
                    root.getChildren().clear();
                    Pane gameover = ui.createGameoverScreen();
                    root.getChildren().add(gameover);
                });

            }
        }

        // 6. Xóa các Entity đã đánh dấu isRemoved
        entities.removeIf(entity ->
                (entity instanceof Bomb && ((Bomb) entity).isRemoved()) ||
                        (entity instanceof Enemy && ((Enemy) entity).isRemoved()) ||
                        (entity instanceof Brick && ((Brick) entity).isRemoved())
        );

        // 7. PAUSE TEST
        if (input.isEscapeJustPressed()) {
            if (timer != null) {
                timer.stop();  // Dừng vòng lặp game
            }
            Pane pause = ui.createPauseScreen();
            root.getChildren().add(pause);

        }

        // 8. NEXT Level TEST
        if (bomberman.intersects(getPortal(stillObjects))) {
            if (bomberman.isAlive()) {
                if (isWincurrentStage()) {
                    timer.stop();
                    currentStage += 1;
                    if (currentStage <= 2) {
                        this.nextLevel(root, currentStage);
                    } else {
                        root.getChildren().clear();
                        Pane win = ui.createVictoryScreen();
                        root.getChildren().add(win);
                    }
                }
            }
        }
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        stillObjects.forEach(g -> g.render(gc));
//        entities.forEach(g -> g.render(gc));
        for (Entity entity : entities) {
            if (entity instanceof Bomber) {
                Bomber bomber = (Bomber) entity;
                if (bomber.isAlive() || !bomber.isDeathAnimationFinished()) {
                    bomber.render(gc);
                }
            } else {
                entity.render(gc);
            }
        }
    }

    //Xu ly enemy chet hoac pha brick tao ra portal va itemddd
    public void replaceEntity(Entity oldEntity, Entity newEntity) {
        int index = stillObjects.indexOf(oldEntity);
        if (index != -1) {
            stillObjects.set(index, newEntity);
        }
    }
}