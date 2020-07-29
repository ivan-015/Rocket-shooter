package edu.utep.cs.cs4381.rocketshooter.model;

import java.util.Random;

public abstract class SpaceObject {

    protected int x;
    protected int y;
    protected int speed;

    protected int MIN_X = 0;
    protected int MIN_Y = 0;

    protected int MAX_X;
    protected int MAX_Y;

    public int width, height;

    protected boolean isActive = true;

    protected static final Random random = new Random();

    public SpaceObject(int x, int y, int screenWidth, int screenHeight, int speed, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.speed = speed;

        MAX_X = screenWidth - width;
        MAX_Y = screenHeight - height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setActive(boolean active) {
        isActive = active;
        x = -height * 2;
    }

    /**
     * Updates the y-coordinate and speed of the object.
     */
    public abstract void update();

}
