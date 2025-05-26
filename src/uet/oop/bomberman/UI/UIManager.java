package uet.oop.bomberman.UI;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Pos;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.Sound.SoundBack;
import uet.oop.bomberman.graphics.Sprite;

import java.util.Timer;
import java.util.TimerTask;

import static uet.oop.bomberman.GameEngine.BombermanGame.HEIGHT;
import static uet.oop.bomberman.GameEngine.BombermanGame.WIDTH;

public class UIManager {
    // Trong class UIScreenManager hoặc class chứa createStartScreen(...)
    public static SoundBack backgroundMusic = new SoundBack("/HUD/backgroundmusic.mp3");
    private GameManager gameManager;
    private AnimationTimer timer;

    // TEST
    private Group root;
    private Stage stage;
    // TEST
    public final Font bricksanFont = Font.loadFont(getClass().getResourceAsStream("/HUD/Brickshapers.ttf"), 60);

    public UIManager(GameManager gameManager, Group root, Stage stage) {
        this.gameManager = gameManager;
        this.root = root;
        this.stage = stage;
    }

    public Pane createStartScreen() {
        Pane startScreen = new Pane();

        // Tải ảnh nền
        Image starterImage = new Image(getClass().getResourceAsStream("/HUD/Background.png"));
        ImageView starterImageView = new ImageView(starterImage);
        starterImageView.setFitWidth(Sprite.SCALED_SIZE * WIDTH); // 480px
        starterImageView.setFitHeight(Sprite.SCALED_SIZE * HEIGHT); // 320px
//        starterImageView.setSmooth(false);
        starterImageView.setPreserveRatio(false);
        startScreen.getChildren().add(starterImageView);

        Image gif1 = new Image(getClass().getResourceAsStream("/HUD/bomberman.gif"));
        ImageView gifView1 = new ImageView(gif1);

        // Tuỳ chỉnh kích thước và vị trí
        gifView1.setFitWidth(250);
        gifView1.setFitHeight(250);
        gifView1.setLayoutX(100);
        gifView1.setLayoutY(330);



        ImageView gifView2 = new ImageView(gif1);

        // Tuỳ chỉnh kích thước và vị trí
        gifView2.setFitWidth(250);
        gifView2.setFitHeight(250);
        gifView2.setLayoutX(640);
        gifView2.setLayoutY(330);

        startScreen.getChildren().add(gifView1);
        startScreen.getChildren().add(gifView2);

        // Tạo VBox để chứa các nút
        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        double canvasWidth = Sprite.SCALED_SIZE * WIDTH; // 480px
        double canvasHeight = Sprite.SCALED_SIZE * HEIGHT; // 320px
        buttonBox.setLayoutX(canvasWidth / 2 - 125); // Căn giữa (250px / 2)
        buttonBox.setLayoutY(250); // Đặt vị trí y bắt đầu từ 250px

        // Tạo nút Play
        Button playButton = new Button("Play");
        playButton.setPrefWidth(250);
        playButton.setPrefHeight(75);
        playButton.setFont(bricksanFont);
        playButton.setStyle("-fx-text-fill: #0c0000; -fx-background-color: transparent;"); // Nền trong suốt
        playButton.setOnAction(event -> {
            root.getChildren().clear();
            gameManager.initialize(root, stage);
        });
        playButton.setOnMouseEntered(event -> {
            startScreen.setCursor(Cursor.HAND);
            playButton.setStyle("-fx-text-fill: #fff8e7; -fx-background-color: transparent;"); // Hiệu ứng hover
        });
        playButton.setOnMouseExited(event -> {
            startScreen.setCursor(Cursor.DEFAULT);
            playButton.setStyle("-fx-text-fill: #0a0101; -fx-background-color: transparent;");
        });

        // Tạo nút Setting
        Button settingButton = new Button("Setting");
        settingButton.setPrefWidth(250);
        settingButton.setPrefHeight(75);
        settingButton.setFont(bricksanFont);
        settingButton.setStyle("-fx-text-fill: #130101; -fx-background-color: transparent;");

        settingButton.setOnAction(event -> {
            root.getChildren().clear();
            Pane settingsScreen = createSettingsScreen();
            root.getChildren().add(settingsScreen);
        });
        settingButton.setOnMouseEntered(event -> {
            startScreen.setCursor(Cursor.HAND);
            settingButton.setStyle("-fx-text-fill: #ffffff; -fx-background-color: transparent;");
        });
        settingButton.setOnMouseExited(event -> {
            startScreen.setCursor(Cursor.DEFAULT);
            settingButton.setStyle("-fx-text-fill: #050000; -fx-background-color: transparent;");
        });

        // Tạo nút Exit
        Button quitButton = new Button("Exit");
        quitButton.setPrefWidth(250);
        quitButton.setPrefHeight(75);
        quitButton.setFont(bricksanFont);
        quitButton.setStyle("-fx-text-fill: #0c0101; -fx-background-color: transparent;");
        quitButton.setOnAction(event -> {
            if (timer != null) {
                timer.stop();
            }
            stage.close();
            Platform.exit();
            System.exit(0);
        });
        quitButton.setOnMouseEntered(event -> {
            startScreen.setCursor(Cursor.HAND);
            quitButton.setStyle("-fx-text-fill: #ffffff; -fx-background-color: transparent;");
        });
        quitButton.setOnMouseExited(event -> {
            startScreen.setCursor(Cursor.DEFAULT);
            quitButton.setStyle("-fx-text-fill: #0a0101; -fx-background-color: transparent;");
        });

        // Phát nhạc nếu chưa phát và bật


        backgroundMusic.play();


        // Thêm các nút vào VBox
        buttonBox.getChildren().addAll(playButton, settingButton, quitButton);
        startScreen.getChildren().add(buttonBox);

        return startScreen;
    }

    public Pane createSettingsScreen() {
        Pane settingsScreen = new Pane();

        Image settingsImage = new Image(getClass().getResourceAsStream("/HUD/setting.png"));
        ImageView settingsImageView = new ImageView(settingsImage);
        settingsImageView.setFitWidth(Sprite.SCALED_SIZE * WIDTH);
        settingsImageView.setFitHeight(Sprite.SCALED_SIZE * HEIGHT);
        settingsImageView.setSmooth(false); // Tắt làm mịn để giữ độ nét
        settingsImageView.setPreserveRatio(false); // Đảm bảo vừa khít canvas
        settingsScreen.getChildren().add(settingsImageView);

        Image backImage = new Image(getClass().getResourceAsStream("/HUD/newcross.png"));
        ImageView backImageView = new ImageView(backImage);
        backImageView.setFitWidth(50); // Kích thước ảnh back (có thể điều chỉnh)
        backImageView.setFitHeight(50);


        // Đặt vị trí ở góc phải trên cùng
        double canvasWidth = Sprite.SCALED_SIZE * WIDTH; // 480px
        backImageView.setX(canvasWidth - backImageView.getFitWidth() - 10); // Cách lề phải 10px
        backImageView.setY(10); // Cách lề trên 10px

        // Xử lý sự kiện nhấp chuột trên ảnh back
        backImageView.setOnMouseClicked(event -> {
            root.getChildren().clear();
            Pane startScreen = createStartScreen();
            root.getChildren().add(startScreen);
        });

        // Thêm hiệu ứng khi chuột di chuyển vào (tùy chọn)
        backImageView.setOnMouseEntered(event -> settingsScreen.setCursor(Cursor.HAND));
        backImageView.setOnMouseExited(event -> settingsScreen.setCursor(Cursor.DEFAULT));

        // Đặt ở đầu class (hoặc truyền vào nếu bạn đã khởi tạo sẵn Sound ở nơi khác)

        Button toggleSoundButton = new Button();
        toggleSoundButton.setLayoutX(75);
        toggleSoundButton.setLayoutY(200);
        toggleSoundButton.setText(backgroundMusic.isOn ? "Background Music: ON" : " Background Music: OFF");

        toggleSoundButton.setPrefWidth(500);
        toggleSoundButton.setPrefHeight(75);
        toggleSoundButton.setFont(bricksanFont);
        toggleSoundButton.setStyle("-fx-text-fill: #0c0101; -fx-background-color: transparent;");
        toggleSoundButton.setOnAction(e -> {
            backgroundMusic.toggle();
            toggleSoundButton.setText(backgroundMusic.isOn ? "Background Music: ON" : " Background Music: OFF");
        });
        toggleSoundButton.setOnMouseEntered(event -> {
            settingsScreen.setCursor(Cursor.HAND);
            toggleSoundButton.setStyle("-fx-text-fill: #fff8e7; -fx-background-color: transparent;"); // Hiệu ứng hover
        });
        toggleSoundButton.setOnMouseExited(event -> {
            settingsScreen.setCursor(Cursor.DEFAULT);
            toggleSoundButton.setStyle("-fx-text-fill: #0a0101; -fx-background-color: transparent;");
        });
        settingsScreen.getChildren().add(toggleSoundButton);


        // Thêm ảnh back vào settingsScreen
        settingsScreen.getChildren().add(backImageView);
        return settingsScreen;
    }

    public Pane createGameoverScreen()  {
        Pane gameoverScreen = new Pane();
        Image gameoverImage = new Image(getClass().getResourceAsStream("/HUD/newGO.png"));
        ImageView gameoverImageView = new ImageView(gameoverImage);
        gameoverImageView.setFitWidth(Sprite.SCALED_SIZE * WIDTH);
        gameoverImageView.setFitHeight(Sprite.SCALED_SIZE * HEIGHT);
        gameoverImageView.setSmooth(false); // Tắt làm mịn để giữ độ nét
        gameoverImageView.setPreserveRatio(false); // Đảm bảo vừa khít canvas
        gameoverScreen.getChildren().add(gameoverImageView);

        int buttonWidth = 250;
        int spacing = 15;
        int totalWidth = buttonWidth * 2 + spacing;

        HBox buttonBox = new HBox(spacing);
        buttonBox.setAlignment(Pos.CENTER);

        double canvasWidth = Sprite.SCALED_SIZE * WIDTH;
        buttonBox.setLayoutX((canvasWidth - totalWidth ) / 2);
        buttonBox.setLayoutY(300);

        Button YesButton = new Button("YES");
        YesButton.setPrefWidth(250);
        YesButton.setPrefHeight(75);
        YesButton.setFont(bricksanFont);
        YesButton.setStyle("-fx-text-fill: #130101; -fx-background-color: transparent;");

        YesButton.setOnAction(event -> {
            root.getChildren().clear();
            this.gameManager.respawn(root, stage);

        });

        YesButton.setOnMouseEntered(event -> {
            gameoverScreen.setCursor(Cursor.HAND);
            YesButton.setStyle("-fx-text-fill: #ffffff; -fx-background-color: transparent;");
        });
        YesButton.setOnMouseExited(event -> {
            gameoverScreen.setCursor(Cursor.DEFAULT);
            YesButton.setStyle("-fx-text-fill: #050000; -fx-background-color: transparent;");
        });


        Button NoButton = new Button("NO");
        NoButton.setPrefWidth(250);
        NoButton.setPrefHeight(75);
        NoButton.setFont(bricksanFont);
        NoButton.setStyle("-fx-text-fill: #130101; -fx-background-color: transparent;");
        NoButton.setOnAction(event -> {
            root.getChildren().clear();
            Pane settingsScreen = createStartScreen();
            root.getChildren().add(settingsScreen);
        });
        NoButton.setOnMouseEntered(event -> {
            gameoverScreen.setCursor(Cursor.HAND);
            NoButton.setStyle("-fx-text-fill: #ffffff; -fx-background-color: transparent;");
        });
        NoButton.setOnMouseExited(event -> {
            gameoverScreen.setCursor(Cursor.DEFAULT);
            NoButton.setStyle("-fx-text-fill: #050000; -fx-background-color: transparent;");
        });

        buttonBox.getChildren().addAll(YesButton, NoButton);
        gameoverScreen.getChildren().add(buttonBox);
        return gameoverScreen;
    }

    public Pane createPauseScreen() {
        Pane pauseScreen = new Pane();

        Image pauseImage = new Image(getClass().getResourceAsStream("/HUD/pause.png"));
        ImageView pauseImageView = new ImageView(pauseImage);
        pauseImageView.setFitWidth(Sprite.SCALED_SIZE * WIDTH);
        pauseImageView.setFitHeight(Sprite.SCALED_SIZE * HEIGHT);
//        pauseImageView.setSmooth(false); // Tắt làm mịn để giữ độ nét
        pauseImageView.setPreserveRatio(false); // Đảm bảo vừa khít canvas
        pauseScreen.getChildren().add(pauseImageView);

        Image backImage = new Image(getClass().getResourceAsStream("/HUD/newcross.png"));
        ImageView backImageView = new ImageView(backImage);
        backImageView.setFitWidth(50); // Kích thước ảnh back (có thể điều chỉnh)
        backImageView.setFitHeight(50);


        // Đặt vị trí ở góc phải trên cùng
        double canvasWidth = Sprite.SCALED_SIZE * WIDTH; // 480px
        backImageView.setX(canvasWidth - backImageView.getFitWidth() - 10); // Cách lề phải 10px
        backImageView.setY(10); // Cách lề trên 10px

        // Xử lý sự kiện nhấp chuột trên ảnh back
        backImageView.setOnMouseClicked(event -> {
            root.getChildren().remove(pauseScreen);
            gameManager.getTimer().start();

        });

        // Thêm hiệu ứng khi chuột di chuyển vào (tùy chọn)
        backImageView.setOnMouseEntered(event -> pauseScreen.setCursor(Cursor.HAND));
        backImageView.setOnMouseExited(event -> pauseScreen.setCursor(Cursor.DEFAULT));
        pauseScreen.getChildren().add(backImageView);


        // Tạo nút
        Button restartButton = new Button("RESTART");
        restartButton.setPrefWidth(300);
        restartButton.setPrefHeight(50);
        restartButton.setLayoutX(80);
        restartButton.setLayoutY(200);
        restartButton.setFont(bricksanFont);
        restartButton.setStyle("-fx-text-fill: #0c0000; -fx-background-color: transparent;"); // Nền trong suốt
        restartButton.setOnAction(event -> {
            root.getChildren().clear();
            this.gameManager.restart(root , gameManager.getCurrentStage());
        });
        restartButton.setOnMouseEntered(event -> {
            pauseScreen.setCursor(Cursor.HAND);
            restartButton.setStyle("-fx-text-fill: #fff8e7; -fx-background-color: transparent;"); // Hiệu ứng hover
        });
        restartButton.setOnMouseExited(event -> {
            pauseScreen.setCursor(Cursor.DEFAULT);
            restartButton.setStyle("-fx-text-fill: #0a0101; -fx-background-color: transparent;");
        });

        Button exitButton = new Button("EXIT");
        exitButton.setPrefWidth(250);
        exitButton.setPrefHeight(50);
        exitButton.setLayoutX(80);
        exitButton.setLayoutY(277);
        exitButton.setFont(bricksanFont);
        exitButton.setStyle("-fx-text-fill: #0c0000; -fx-background-color: transparent;"); // Nền trong suốt
        exitButton.setOnAction(event -> {
            root.getChildren().clear();
            Pane startScreen = gameManager.getUI().createStartScreen();
            root.getChildren().add(startScreen);
        });
        exitButton.setOnMouseEntered(event -> {
            pauseScreen.setCursor(Cursor.HAND);
            exitButton.setStyle("-fx-text-fill: #fff8e7; -fx-background-color: transparent;"); // Hiệu ứng hover
        });
        exitButton.setOnMouseExited(event -> {
            pauseScreen.setCursor(Cursor.DEFAULT);
            exitButton.setStyle("-fx-text-fill: #0a0101; -fx-background-color: transparent;");
        });

        Button toggleSoundButton = new Button();
        toggleSoundButton.setLayoutX(80);
        toggleSoundButton.setLayoutY(350);
        toggleSoundButton.setText(backgroundMusic.isOn ? "Background Music: ON" : " Background Music: OFF");

        toggleSoundButton.setPrefWidth(500);
        toggleSoundButton.setPrefHeight(50);
        toggleSoundButton.setFont(bricksanFont);
        toggleSoundButton.setStyle("-fx-text-fill: #0c0101; -fx-background-color: transparent;");
        toggleSoundButton.setOnAction(e -> {
            backgroundMusic.toggle();
            toggleSoundButton.setText(backgroundMusic.isOn ? "Background Music: ON" : " Background Music: OFF");
        });
        toggleSoundButton.setOnMouseEntered(event -> {
            pauseScreen.setCursor(Cursor.HAND);
            toggleSoundButton.setStyle("-fx-text-fill: #fff8e7; -fx-background-color: transparent;"); // Hiệu ứng hover
        });
        toggleSoundButton.setOnMouseExited(event -> {
            pauseScreen.setCursor(Cursor.DEFAULT);
            toggleSoundButton.setStyle("-fx-text-fill: #0a0101; -fx-background-color: transparent;");
        });
        pauseScreen.getChildren().add(restartButton);
        pauseScreen.getChildren().add(exitButton);
        pauseScreen.getChildren().add(toggleSoundButton);


        return pauseScreen;
    }

    public Pane createVictoryScreen() {
        Pane winScreen = new Pane();

        // Tải ảnh nền
        Image winImage = new Image(getClass().getResourceAsStream("/HUD/win.png"));
        ImageView winImageView = new ImageView(winImage);
        winImageView.setFitWidth(Sprite.SCALED_SIZE * WIDTH); // 480px
        winImageView.setFitHeight(Sprite.SCALED_SIZE * HEIGHT); // 320px
//        starterImageView.setSmooth(false);
        winImageView.setPreserveRatio(false);
        winScreen.getChildren().add(winImageView);

        Image gif1 = new Image(getClass().getResourceAsStream("/HUD/bomberman.gif"));
        ImageView gifView1 = new ImageView(gif1);

        // Tuỳ chỉnh kích thước và vị trí
        gifView1.setFitWidth(250);
        gifView1.setFitHeight(250);
        gifView1.setLayoutX(100);
        gifView1.setLayoutY(330);



        ImageView gifView2 = new ImageView(gif1);

        // Tuỳ chỉnh kích thước và vị trí
        gifView2.setFitWidth(250);
        gifView2.setFitHeight(250);
        gifView2.setLayoutX(640);
        gifView2.setLayoutY(330);

        winScreen.getChildren().add(gifView1);
        winScreen.getChildren().add(gifView2);


        // Tạo VBox để chứa các nút
        VBox buttonBox = new VBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        double canvasWidth = Sprite.SCALED_SIZE * WIDTH; // 480px

        buttonBox.setLayoutX(canvasWidth / 2 - 125); // Căn giữa (250px / 2)
        buttonBox.setLayoutY(300); // Đặt vị trí y bắt đầu từ 250px

        // Tạo nút Play
        Button homeButton = new Button("HOME");
        homeButton.setPrefWidth(250);
        homeButton.setPrefHeight(75);
        homeButton.setFont(bricksanFont);
        homeButton.setStyle("-fx-text-fill: #0c0000; -fx-background-color: transparent;"); // Nền trong suốt
        homeButton.setOnAction(event -> {
            root.getChildren().clear();
            Pane startScreen = gameManager.getUI().createStartScreen();
            root.getChildren().add(startScreen);
        });
        homeButton.setOnMouseEntered(event -> {
            winScreen.setCursor(Cursor.HAND);
            homeButton.setStyle("-fx-text-fill: #fff8e7; -fx-background-color: transparent;"); // Hiệu ứng hover
        });
        homeButton.setOnMouseExited(event -> {
            winScreen.setCursor(Cursor.DEFAULT);
            homeButton.setStyle("-fx-text-fill: #0a0101; -fx-background-color: transparent;");
        });

        // Thêm các nút vào VBox
        buttonBox.getChildren().addAll(homeButton);
        winScreen.getChildren().add(buttonBox);

        return winScreen;
    }

}
