package edu.utep.cs.cs4381.rocketshooter.model;

import android.graphics.Point;

import java.util.zip.CheckedInputStream;

public class Bullet {

    private final static int HEIGHT = 10;
    private final static int WIDTH = 5;

    public static final int DOWN = 1;
    public static final int UP = -1;

    private int direction;
    private int x;
    private int y;
    private float yVelocity;

    private Hitbox hitbox;

    public Bullet(int x, int y, int speed, int direction) {
        this.direction = direction;
        this.x = x;
        this.y = y;
        this.yVelocity = speed * direction;
        hitbox = new Hitbox(new Point(x, y), new Point(x + WIDTH, y + HEIGHT));
    }

    public int getDirection() {
        return direction;
    }

    public void update() {
        y += yVelocity;
        hitbox.update(new Point(x, y), new Point(x + WIDTH, y + HEIGHT));
    }

    public void hideBullet() {
        this.y = -50;
        yVelocity = 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }
}
