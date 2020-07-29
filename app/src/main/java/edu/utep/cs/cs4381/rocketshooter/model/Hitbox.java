package edu.utep.cs.cs4381.rocketshooter.model;

import android.graphics.Point;
import android.graphics.Rect;

public class Hitbox {

    private Rect hitbox;

    public Hitbox(Point topLeftCorner, Point bottomRightCorner) {
        hitbox = new Rect(topLeftCorner.x, topLeftCorner.y, bottomRightCorner.x, bottomRightCorner.y);
    }

    public void update(Point topLeftCorner, Point bottomRightCorner) {
        hitbox.left = topLeftCorner.x;
        hitbox.top = topLeftCorner.y;

        hitbox.right = bottomRightCorner.x;
        hitbox.bottom = bottomRightCorner.y;
    }

    public boolean intersects(Hitbox box) {
        return this.hitbox.intersect(box.getRect());
    }

    public Rect getRect() {
        return hitbox;
    }

}
