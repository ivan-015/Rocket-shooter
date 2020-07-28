package edu.utep.cs.cs4381.rocketshooter.model;

public class Star extends SpaceObject {

    public Star(int size, int screenWidth, int screenHeight, int speed) {
        super(random.nextInt(screenWidth), random.nextInt(screenHeight), screenWidth, screenHeight, speed, size, size);
    }

    public void update() {
        super.update();
    }
}
