package edu.utep.cs.cs4381.rocketshooter.model;

import android.graphics.Point;

public abstract class Powerup extends SpaceObject {
    private Hitbox hitbox;

    public Powerup(int x, int y, int screenWidth, int screenHeight, int speed, int width, int height) {
        super(x, y, screenWidth, screenHeight, speed, width, height);
        hitbox = new Hitbox(new Point(x, y), new Point(x + width, y + height));
    }

    @Override
    public void update() {
        hitbox.update(new Point(x, y), new Point(x + width, y + height));
        y += speed;
        if (y >= MAX_Y) {
            isActive = false;
        }
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void deactivate() {
        isActive = false;
    }

    /// Applies effect of the powerup
    public abstract void upgradePlayer(PlayerShip player);

    public Hitbox getHitbox() {
        return hitbox;
    }
}
