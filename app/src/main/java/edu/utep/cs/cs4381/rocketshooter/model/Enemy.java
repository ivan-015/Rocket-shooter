package edu.utep.cs.cs4381.rocketshooter.model;

import android.content.Context;

public class Enemy extends Ship {

    private static final int MAX_SPEED = 1;

    public static final int WIDTH = 100;
    public static final int HEIGHT = 100;

    public Enemy(int x, int y, int screenWidth, int screenHeight) {
        super(x, y, MAX_SPEED, screenWidth, screenHeight, WIDTH, HEIGHT);
        fireRate = random.nextFloat();
    }

    public void update(PlayerShip player, Context context) {
        super.update();

        // Check if enemy aligns with the player
        if ((player.x >= this.x && player.x <= this.x + this.width) ||
                (this.x >= player.x && this.x <= player.x + player.width)) {
            // Random chance to shoot
            if (random.nextInt(250) == 0) {
                super.shoot(Bullet.DOWN);
                SoundPlayer.instance(context).play(SoundPlayer.Sound.ENEMY_SHOOT);
            }
        }

        // Random low chance to shoot even if not near player
        if (random.nextInt(600) == 0 && isActive) {
            super.shoot(Bullet.DOWN);
            SoundPlayer.instance(context).play(SoundPlayer.Sound.ENEMY_SHOOT);
        }
    }
}
