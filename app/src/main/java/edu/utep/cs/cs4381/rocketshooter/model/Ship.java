package edu.utep.cs.cs4381.rocketshooter.model;

import android.graphics.Bitmap;

public class Ship extends SpaceObject {
    public Ship(int x, int y, int speed, int screenWidth, int screenHeight, int width, int height) {
        super(x, y, screenWidth, screenHeight, speed, width, height);
    }

    public void shoot() {
    }

    public void stopMoving() {
        speed = 0;
    }
}
