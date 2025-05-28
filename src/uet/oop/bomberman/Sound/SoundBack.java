package uet.oop.bomberman.Sound;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.URL;

public class SoundBack {
    private MediaPlayer mediaPlayer;
    public boolean isOn = true;

    public SoundBack(String resourcePath) {
        try {
            URL resource = getClass().getResource(resourcePath);
            if (resource == null) {
                System.out.println("Không tìm thấy file nhạc: " + resourcePath);
                return;
            }

            Media media = new Media(resource.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(0.4);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (isOn && mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void toggle() {
        isOn = !isOn;
        if (!isOn) {
            stop();
        } else {
            play();
        }
    }


}
