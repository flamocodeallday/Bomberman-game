package uet.oop.bomberman.UI;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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



    public UIManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public Pane createStartScreen(Group root, Stage stage) {
        Pane startScreen = new Pane();

        // Tải ảnh nền
        Image starterImage = new Image(getClass().getResourceAsStream("/HUD/Background.png"));
        ImageView starterImageView = new ImageView(starterImage);
        starterImageView.setFitWidth(Sprite.SCALED_SIZE * WIDTH); // 480px
        starterImageView.setFitHeight(Sprite.SCALED_SIZE * HEIGHT); // 320px
        starterImageView.setSmooth(false);
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


        Font bricksanFont = Font.loadFont(getClass().getResourceAsStream("/HUD/Brickshapers.ttf"), 60);
        if (bricksanFont == null) {
            System.err.println("Font Bricksan-Regular.ttf not found!");
        }

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
            playButton.setText("Playing...");
            playButton.setStyle("-fx-text-fill: #fff8e7; -fx-background-color: transparent;"); // Đổi màu chữ
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        playButton.setText("Play");
                        playButton.setStyle("-fx-text-fill: #0a0101; -fx-background-color: transparent;");

                        root.getChildren().clear();
                        gameManager.initialize(root, stage);
                    });
                }
            }, 1000);
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
            Pane settingsScreen = createSettingsScreen(root, stage);
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

    /** CREATE SETTING SCREEN. */
    public Pane createSettingsScreen(Group root, Stage stage) {
        Pane settingsScreen = new Pane();

        Image settingsImage = new Image(getClass().getResourceAsStream("/HUD/setting1.png"));
        ImageView settingsImageView = new ImageView(settingsImage);
        settingsImageView.setFitWidth(Sprite.SCALED_SIZE * WIDTH);
        settingsImageView.setFitHeight(Sprite.SCALED_SIZE * HEIGHT);
        settingsImageView.setSmooth(false); // Tắt làm mịn để giữ độ nét
        settingsImageView.setPreserveRatio(false); // Đảm bảo vừa khít canvas
        settingsScreen.getChildren().add(settingsImageView);

        Image backImage = new Image(getClass().getResourceAsStream("/HUD/cross.png"));
        ImageView backImageView = new ImageView(backImage);
        backImageView.setFitWidth(50); // Kích thước ảnh back (có thể điều chỉnh)
        backImageView.setFitHeight(50);

        Rectangle2D backRegion = new Rectangle2D(650, 30, 75, 75);

        // Đặt vị trí ở góc phải trên cùng
        double canvasWidth = Sprite.SCALED_SIZE * WIDTH; // 480px
        double canvasHeight = Sprite.SCALED_SIZE * HEIGHT; // 320px
        backImageView.setX(canvasWidth - backImageView.getFitWidth() - 10); // Cách lề phải 10px
        backImageView.setY(10); // Cách lề trên 10px

        // Xử lý sự kiện nhấp chuột trên ảnh back
        backImageView.setOnMouseClicked(event -> {
            root.getChildren().clear();
            Pane startScreen = createStartScreen(root, stage);
            root.getChildren().add(startScreen);
        });

        // Thêm hiệu ứng khi chuột di chuyển vào (tùy chọn)
        backImageView.setOnMouseEntered(event -> settingsScreen.setCursor(Cursor.HAND));
        backImageView.setOnMouseExited(event -> settingsScreen.setCursor(Cursor.DEFAULT));

        // Đặt ở đầu class (hoặc truyền vào nếu bạn đã khởi tạo sẵn Sound ở nơi khác)

        Button toggleSoundButton = new Button();
        toggleSoundButton.setPrefSize(200, 50);
        toggleSoundButton.setLayoutX(canvasWidth / 2 - 100);
        toggleSoundButton.setLayoutY(200);
        toggleSoundButton.setText(backgroundMusic.isOn ? "Music: ON" : "Music: OFF");

        toggleSoundButton.setOnAction(e -> {
            backgroundMusic.toggle();
            toggleSoundButton.setText(backgroundMusic.isOn ? "Music: ON" : "Music: OFF");
        });

        settingsScreen.getChildren().add(toggleSoundButton);


        // Thêm ảnh back vào settingsScreen
        settingsScreen.getChildren().add(backImageView);
        return settingsScreen;
    }


}
