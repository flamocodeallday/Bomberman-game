package uet.oop.bomberman.GameEngine;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import uet.oop.bomberman.Input;
import uet.oop.bomberman.UI.UIManager;
import uet.oop.bomberman.entities.Bomb.Bomb;
import uet.oop.bomberman.entities.World.Bomber;
import uet.oop.bomberman.entities.World.Entity;
import uet.oop.bomberman.entities.World.Grass;
import uet.oop.bomberman.entities.World.Wall;
import uet.oop.bomberman.entities.World.Brick;
import uet.oop.bomberman.graphics.Sprite;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class BombermanGame extends Application {

    public static final int WIDTH = 30;
    public static final int HEIGHT = 20;


    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    @Override
    public void start(Stage stage) {


        // Tao root container
        Group root = new Group();

        // Tao scene
        Scene scene = new Scene(root);
        stage.setScene(scene);

        Input input = new Input();
        scene.setOnKeyPressed(input::handlePressed);
        scene.setOnKeyReleased(input::handleReleased);
        GameManager game = new GameManager(input);
        UIManager ui = new UIManager(game,root, stage);

        //TEST
        game.setUI(ui,root,stage);

        Pane startScreen = ui.createStartScreen();
        root.getChildren().add(startScreen);

        // Them scene vao stage

        stage.show();


    }


}