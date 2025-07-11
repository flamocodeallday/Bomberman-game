package uet.oop.bomberman.Sound;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundEffect {
    private Media media;
    private static boolean isOn = true;

    public SoundEffect(String resourcePath) {
        try {
            URL resource = getClass().getResource(resourcePath);
            if (resource == null) {
                System.out.println("Không tìm thấy file âm thanh: " + resourcePath);
                return;
            }
            media = new Media(resource.toURI().toString());
        } catch (Exception e) {
            System.out.println("Lỗi khi tải âm thanh: " + resourcePath);
            e.printStackTrace();
        }
    }

    public void play() {
        if (!isOn || media == null) {
            return;
        }
        try {
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.dispose());
            mediaPlayer.setVolume(0.9);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Lỗi khi phát âm thanh");
            e.printStackTrace();
        }
    }

}