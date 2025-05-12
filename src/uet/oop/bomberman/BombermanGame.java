package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.entities.Bomber;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.Grass;
import uet.oop.bomberman.entities.Wall;
import uet.oop.bomberman.entities.Brick;
import uet.oop.bomberman.graphics.Sprite;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class BombermanGame extends Application {

    public static final int WIDTH = 30;
    public static final int HEIGHT = 20;
    private GraphicsContext gc;
    private Canvas canvas;
    private List<Entity> entities = new ArrayList<>();
    private List<Entity> stillObjects = new ArrayList<>();

    private List<Bomb> bombsAdd = new ArrayList<>(); //List trung gian
    private List<Entity> toRemove = new ArrayList<>();

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

    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    @Override
    public void start(Stage stage) {
        Input input = new Input();

        // Tao Canvas
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // Tao root container
        Group root = new Group();
        root.getChildren().add(canvas);

        // Tao scene
        Scene scene = new Scene(root);

        // Them scene vao stage
        stage.setScene(scene);
        stage.show();


        scene.setOnKeyPressed(input::handlePressed);
        scene.setOnKeyReleased(input::handleReleased);
        canvas.requestFocus();

        createMap();

        Entity bomberman = new Bomber(1, 1, Sprite.player_right.getFxImage(), input, this);
        entities.add(bomberman);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                render();
                update();
            }
        };

        timer.start();
    }

    public void createMap() {
        try {
            // Đọc tệp map.txt
            BufferedReader reader = new BufferedReader(new FileReader("D://Code//Bombermannn_Project//res//levels//Level1.txt"));
            String line;
            int j = 0;

            while ((line = reader.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    Entity object;

                    switch (c) {
                        case '#':
                            object = new Wall(i, j, Sprite.wall.getFxImage());
                            break;
                        case '*':
                            object = new Brick(i, j, Sprite.brick.getFxImage(), this);
                            break;
                        default:
                            object = new Grass(i, j, Sprite.grass.getFxImage());
                            break;
                    }

                    stillObjects.add(object);
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