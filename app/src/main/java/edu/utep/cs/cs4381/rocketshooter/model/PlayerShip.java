package edu.utep.cs.cs4381.rocketshooter.model;

import android.graphics.Bitmap;

public class PlayerShip extends Ship {

    private int MAX_SPEED = 10;

    private int LIVES = 3;

    public PlayerShip(int x, int y, int screenWidth, int screenHeight) {
        super(x, y, 0, screenWidth, screenHeight, 200, 200);
    }

    @Override
    public void update() {
        super.update();
        x += speed;

        // Keep player within screen bounds
        if (x >= MAX_X) {
            x = MAX_X;
        } else if (x <= MIN_X) {
            x = 0;
        }

    }

    public void moveRight() {
        speed = MAX_SPEED;
    }

    public void moveLeft() {
        speed = -MAX_SPEED;
    }

    public void takeLife() {
        LIVES--;
    }

    public int getLives() {
        return LIVES;
    }
}
