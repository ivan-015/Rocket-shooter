package edu.utep.cs.cs4381.rocketshooter.model;

public class PlayerShip extends Ship {

    private int MAX_SPEED = 10;

    private int LIVES = 3;

    public int score = 0;

    public PlayerShip(int x, int y, int screenWidth, int screenHeight) {
        super(x, y, 0, screenWidth, screenHeight, 200, 200);
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

    public void addLife() {
        LIVES++;
    }

    public int getLives() {
        return LIVES;
    }
}
