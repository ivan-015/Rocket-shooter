package edu.utep.cs.cs4381.rocketshooter.model;

public class Star extends SpaceObject {

    public Star(int size, int screenWidth, int screenHeight, int speed) {
        super(random.nextInt(screenWidth), random.nextInt(screenHeight), screenWidth, screenHeight, speed, size, size);
    }

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
