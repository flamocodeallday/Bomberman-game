package uet.oop.bomberman.entities.Item;

import uet.oop.bomberman.entities.Bomber;
import uet.oop.bomberman.graphics.Sprite;

public class FlameItem extends Item {
    public FlameItem(int x, int y) {
        super(x, y, Sprite.powerup_flames.getFxImage());
    }

    @Override
    public void applyEffect(Bomber bomber) {
        bomber.increaseFlameLength();
    }

    @Override
    public void update() {
        //
    }
}
