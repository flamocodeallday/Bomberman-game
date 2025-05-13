module bomberman {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires java.desktop;
    requires javafx.media;

    opens uet.oop.bomberman;
    opens uet.oop.bomberman.UI;
    opens uet.oop.bomberman.Sound;
    opens uet.oop.bomberman.GameEngine;
}