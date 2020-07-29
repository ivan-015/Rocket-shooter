package edu.utep.cs.cs4381.rocketshooter.model;

import android.graphics.Point;

public class Barrier extends SpaceObject {

    protected Hitbox hitbox;

    public Barrier(int x, int y, int screenWidth, int screenHeight, int width, int height) {
        super(x, y, screenWidth, screenHeight, 0, width, height);
        hitbox = new Hitbox(new Point(x, y), new Point(x + width, y + height));
    }

    @Override
    public void update() {
        hitbox.update(new Point(x,y), new Point(x+width, y + height));
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

}
