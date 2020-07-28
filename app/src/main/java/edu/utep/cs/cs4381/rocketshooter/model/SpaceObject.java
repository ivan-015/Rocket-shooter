package edu.utep.cs.cs4381.rocketshooter.model;

import java.util.Random;

public class SpaceObject {

    protected int x;
    protected int y;
    protected int speed;

    protected int MIN_X = 0;
    protected int MIN_Y = 0;

    protected int MAX_X;
    protected int MAX_Y;

    public int width, height;

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


    /**
     * Updates the y-coordinate and speed of the object.
     */
    public void update() {
        y += speed;

        // Reset position of object
        if (y > MAX_Y) {
            speed = random.nextInt(10) + 10;
            x = random.nextInt(MAX_X);
            y = -height;
        }
    }

}
