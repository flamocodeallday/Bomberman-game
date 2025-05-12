package uet.oop.bomberman.entities.Item;

import uet.oop.bomberman.entities.Bomber;
import uet.oop.bomberman.graphics.Sprite;


public class SpeedItem extends Item {
    public SpeedItem(int x, int y) {
        super(x, y, Sprite.powerup_speed.getFxImage());
    }

    @Override
    public void applyEffect(Bomber bomber) {
        bomber.increaseSpeed();
    }

    @Override
    public void update() {
        //
    }
}
