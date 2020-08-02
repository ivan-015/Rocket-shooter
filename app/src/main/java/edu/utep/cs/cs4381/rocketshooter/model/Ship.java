package edu.utep.cs.cs4381.rocketshooter.model;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.utep.cs.cs4381.rocketshooter.model.Bullet;
import edu.utep.cs.cs4381.rocketshooter.model.Hitbox;
import edu.utep.cs.cs4381.rocketshooter.model.SpaceObject;

public abstract class Ship extends SpaceObject {

    protected int maxBullets = 5;
    protected int numBullets;
    protected int next;
    protected float fireRate = 1;
    protected long lastShotTime;

    protected List<Bullet> bullets;

    protected Hitbox hitbox;

    protected boolean hitLimit = false;

    public Ship(int x, int y, int speed, int screenWidth, int screenHeight, int width, int height) {
        super(x, y, screenWidth, screenHeight, speed, width, height);
        bullets = new CopyOnWriteArrayList<>();
        lastShotTime = -1;
        next = -1;

        hitbox = new Hitbox(new Point(x, y), new Point(x + screenWidth, y + screenHeight));
    }

    @Override
    /// Updates position and bullets of ship
    public void update() {
        x += speed;

        // Keep player within screen bounds
        if (x >= MAX_X && isActive) {
            x = MAX_X;
            hitLimit = true;
        } else if (x <= MIN_X && isActive) {
            x = MIN_X;
            hitLimit = true;
        } else {
            hitLimit = false;
        }

        for (Bullet bullet : bullets) {
            bullet.update();
        }
        hitbox.update(new Point(x, y), new Point(x + width, y + height));
    }

    public boolean shoot(int direction) {
        boolean shotFired = false;
        if (System.currentTimeMillis() - lastShotTime > 1000 / fireRate) {
            next++;
            if (numBullets > maxBullets) {
                numBullets = maxBullets;
            }
            if (next == maxBullets) {
                next = 0;
            }
            lastShotTime = System.currentTimeMillis();
            bullets.add(next, new Bullet(x + width / 2, y, 25, direction));

            shotFired = true;
            numBullets++;
        }
        return shotFired;
    }

    public void stopMoving() {
        speed = 0;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public boolean getHitLimit() {
        return hitLimit;
    }
}
