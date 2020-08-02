package edu.utep.cs.cs4381.rocketshooter.model;

public class ExtraLife extends Powerup {

    public static final int SPEED = 3;
    public static final int WIDTH = 100;
    public static final int HEIGHT = WIDTH;

    public ExtraLife(int x, int y, int screenWidth, int screenHeight) {
        super(x, y, screenWidth, screenHeight, SPEED, WIDTH, HEIGHT);
    }

    @Override
    public void upgradePlayer(PlayerShip player) {
        player.addLife();
    }
}
