package uet.oop.bomberman.entities.Item;

import uet.oop.bomberman.entities.World.Bomber;
import uet.oop.bomberman.graphics.Sprite;


public class BombItem extends Item {
    public BombItem(int x, int y) {
        super(x, y, Sprite.powerup_bombs.getFxImage());
    }

    @Override
    public void applyEffect(Bomber bomber) {
        bomber.increaseBombPlace();
    }

    @Override
    public void update() {
        // Không cần làm gì nếu item không có animation hoặc hành động riêng
    }
}
