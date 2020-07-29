package edu.utep.cs.cs4381.rocketshooter.model;

import android.graphics.Bitmap;

public class Enemy extends Ship {

    private int MAX_SPEED = 2;

    public Enemy(int x, int y, int screenWidth, int screenHeight) {
        super(x, y, 0, screenWidth, screenHeight, 200, 100);
    }

    @Override
    public void update() {
        super.update();
    }
}
