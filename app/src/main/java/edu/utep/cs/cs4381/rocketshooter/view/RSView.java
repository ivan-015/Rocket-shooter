package edu.utep.cs.cs4381.rocketshooter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.utep.cs.cs4381.rocketshooter.model.Barrier;
import edu.utep.cs.cs4381.rocketshooter.model.Bullet;
import edu.utep.cs.cs4381.rocketshooter.model.Enemy;
import edu.utep.cs.cs4381.rocketshooter.model.ExtraLife;
import edu.utep.cs.cs4381.rocketshooter.model.PlayerShip;
import edu.utep.cs.cs4381.rocketshooter.model.Powerup;
import edu.utep.cs.cs4381.rocketshooter.model.SoundPlayer;
import edu.utep.cs.cs4381.rocketshooter.model.SpaceObject;
import edu.utep.cs.cs4381.rocketshooter.model.Star;

public class RSView extends SurfaceView implements Runnable {

    Context context;

    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;

    private boolean isRunning = true;
    private boolean isGameOver = false;
    private boolean playerWon = false;

    private int screenWidth, screenHeight;

    private Thread thread;

    /// Entities in the game
    private PlayerShip player;
    private List<Enemy> enemies;
    private List<Barrier> barriers;
    private List<Star> stars;
    private List<Powerup> powerups;

    /// Rectangles representing left and right movement buttons
    private RectF LEFT_BUTTON;
    private RectF RIGHT_BUTTON;

    /// Top Left Corner coordinates for the pause button
    private Point PAUSE_TLC;
    /// Bottom Right Corner coordinates for the pause button
    private Point PAUSE_BRC;

    /// Number of enemies that are alive
    private int activeEnemies;
    /// Number of enemy rows and columns
    private final int NUM_ENEMY_ROWS = 4;
    private final int NUM_ENEMY_COLS = 7;

    /// Number of enemy groups
    private final int NUM_BARRIER_GROUPS = 3;
    /// Number of enemy rows inside each group
    private final int NUM_BARRIER_ROWS = 7;
    /// Number of enemy columns inside each group
    private final int NUM_BARRIER_COLS = 8;

    private Random random = new Random();

    private boolean firstTimePlaying = true;

    public RSView(Context context, int screenWidth, int screenHeight) {
        super(context);

        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        paint = new Paint();
        holder = getHolder();

        enemies = new CopyOnWriteArrayList<>();
        barriers = new CopyOnWriteArrayList<>();
        stars = new CopyOnWriteArrayList<>();
        powerups = new CopyOnWriteArrayList<>();

        LEFT_BUTTON = new RectF(10, screenHeight * .86f, screenWidth * .32f, screenHeight - 80);
        RIGHT_BUTTON = new RectF(screenWidth - (screenWidth * .32f), screenHeight * .86f, screenWidth - 10, screenHeight - 80);

        PAUSE_TLC = new Point(screenWidth / 2 - 50, 50);
        PAUSE_BRC = new Point(screenWidth / 2 + 50, 150);

        start();
    }

    /**
     * Sets up all entities that will be present in the game
     */
    private void start() {
        Random random = new Random();
        //Clear lists from previous game
        enemies.clear();
        barriers.clear();
        stars.clear();
        powerups.clear();


        player = new PlayerShip(screenWidth / 2 - 100, (int) LEFT_BUTTON.top - 50, screenWidth, screenHeight);

        //Create enemies
        for (int col = 0; col < NUM_ENEMY_COLS; col++) {
            for (int row = 0; row < NUM_ENEMY_ROWS; row++) {
                enemies.add(new Enemy(colIndexToScreen(col, Enemy.WIDTH), rowIndexToScreen(row, Enemy.HEIGHT), screenWidth, screenHeight));
            }
        }

        activeEnemies = enemies.size();

        //Create stars
        for (int i = 0; i < 60; i++) {
            int speed = random.nextInt(10);
            speed = speed == 0 ? 2 : speed;
            stars.add(new Star(3, screenWidth, screenHeight, speed));
        }

        // Create barriers
        for (int group = 0; group < NUM_BARRIER_GROUPS; group++) {
            for (int col = 0; col < NUM_BARRIER_COLS; col++) {
                for (int row = 0; row < NUM_BARRIER_ROWS; row++) {
                    int width = (screenWidth / NUM_BARRIER_GROUPS) / NUM_BARRIER_COLS - 10;
                    int height = width;
                    int x = barrierIndexColToScreen(group, col, width);
                    int y = barrierIndexRowToScreen(row, height);


                    barriers.add(new Barrier(x, y, screenWidth, screenHeight, width, height));

                }
            }
        }

        isRunning = true;

        // Show instructions on first time playing
        if (firstTimePlaying) {
            isGameOver = true;
        }
        // Jump straight into the game on subsequent playthroughs
        else {
            isGameOver = false;
        }
        playerWon = false;
    }

    /**
     * Returns the x position of the barrier depending on which
     * [group] and what [index] it is in
     *
     * @param group
     * @param index
     * @return
     */
    private int barrierIndexColToScreen(int group, int index, int barrierWidth) {
        int GROUP_WIDTH = screenWidth / NUM_BARRIER_GROUPS;
        return group * GROUP_WIDTH + index * barrierWidth + 30;
    }

    /**
     * Returns the y position of the barrier depending on which
     * [group] and what [index] it is in
     *
     * @param index
     * @return
     */
    private int barrierIndexRowToScreen(int index, int barrierHeight) {
        return screenHeight / 2 + index * barrierHeight;
    }

    private int colIndexToScreen(int index, int enemyWidth) {
        return (index * enemyWidth) + (20 * index);
    }

    private int rowIndexToScreen(int index, int enemyHeight) {
        return (index * enemyHeight) + (20 * index) + 100;
    }

    @Override
    public void run() {
        while (isRunning) {
            update();
            draw();
            control();
        }
    }

    /**
     * Updates entities and checks for collisions
     */
    private void update() {
        boolean bounceEnemies = false;

        if (!isGameOver) {

            // Update all entities
            player.update();
            for (Star star : stars) {
                star.update();
            }
            for (Enemy enemy : enemies) {
                enemy.update(player, context);
                if (enemy.getHitLimit()) {
                    bounceEnemies = true;
                }
            }
            for (Barrier barrier : barriers) {
                barrier.update();
            }
            for (Powerup powerup : powerups) {
                powerup.update();
                if (!powerup.isActive()) {
                    powerups.remove(powerup);
                }
            }

            //Check if player intersects with powerup
            for (Powerup powerup : powerups) {
                if (player.getHitbox().intersects(powerup.getHitbox())) {
                    powerup.upgradePlayer(player);
                    powerup.deactivate();
                    SoundPlayer.instance(context).play(SoundPlayer.Sound.EXTRA_LIFE);
                }
            }

            // Check if player hit barrier or enemy
            for (Bullet bullet : player.getBullets()) {
                for (Barrier barrier : barriers) {
                    // Bullet hit barrier
                    if (bullet.getHitbox().intersects(barrier.getHitbox())) {
                        bullet.hideBullet();
                        barrier.deactivate();
                        SoundPlayer.instance(context).play(SoundPlayer.Sound.BARRIER_HIT);
                    }
                }
                for (Enemy enemy : enemies) {

                    // Bullet hit enemy
                    if (bullet.getHitbox().intersects(enemy.getHitbox())) {
                        // Random chance to get powerup when enemy is killed
                        if (random.nextInt(4) == 0) {
                            powerups.add(new ExtraLife(enemy.getX() + enemy.width / 2,
                                    enemy.getY() + enemy.height, screenWidth, screenHeight));
                        }
                        bullet.hideBullet();
                        enemy.deactivate();
                        player.score += player.getLives() * 100;
                        SoundPlayer.instance(context).play(SoundPlayer.Sound.ENEMY_EXPLOSION);
                        activeEnemies--;


                    }
                }

            }

            // Check if the player won by eliminating all enemies
            if (activeEnemies <= 0) {
                playerWon = true;
                isGameOver = true;
                SoundPlayer.instance(context).play(SoundPlayer.Sound.PLAYER_WIN);
            }

            // Check if enemy hit barrier or player
            for (Enemy enemy : enemies) {
                if (bounceEnemies) {
                    enemy.setSpeed(-enemy.getSpeed());
                }
                for (Bullet bullet : enemy.getBullets()) {
                    for (Barrier barrier : barriers) {
                        // Bullet hits barrier
                        if (bullet.getHitbox().intersects(barrier.getHitbox())) {
                            barrier.deactivate();
                            bullet.hideBullet();
                            SoundPlayer.instance(context).play(SoundPlayer.Sound.BARRIER_HIT);
                        }
                    }
                    // Bullet hits player
                    if (bullet.getHitbox().intersects(player.getHitbox())) {
                        bullet.hideBullet();
                        player.takeLife();
                        SoundPlayer.instance(context).play(SoundPlayer.Sound.PLAYER_EXPLOSION);
                    }
                }
            }
        }
        // Check for game over
        if (player.getLives() <= 0) {
            isGameOver = true;
        }
    }

    /**
     * Draws entities on the screen
     */
    private void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            //Draw background
            canvas.drawColor(Color.BLACK);

            //Draw stars
            drawStars();

            //Draw player
            drawPlayer(player);

            //Draw enemies
            drawEnemies();

            //Draw barriers
            drawBarriers();

            drawPowerUps();

            drawBullets();

            drawButtons();

            drawHUD();

            if (!isGameOver) {
                drawPauseButton();
            }
            if (!isRunning) {
                drawPauseMenu();
            }

            // draw winning menu for player
            if (playerWon) {
                drawPlayerWon();
            }
            // Draw starting menu for player
            else if (isGameOver && firstTimePlaying) {
                drawWelcome();
            }
            // draw losing menu for player
            else if (isGameOver) {
                drawGameOver();
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawPauseButton() {
        paint.setAlpha(100);
        canvas.drawOval(PAUSE_TLC.x, PAUSE_TLC.y, PAUSE_BRC.x, PAUSE_BRC.y, paint);

        paint.setAlpha(150);
        int PAUSE_TOP = PAUSE_TLC.y + 20;
        int PAUSE_BOTTOM = PAUSE_BRC.y - 20;
        // Draw Pause Button
        if (isRunning) {

            canvas.drawRect(PAUSE_TLC.x + 20, PAUSE_TOP, PAUSE_BRC.x - 60, PAUSE_BOTTOM, paint);
            canvas.drawRect(PAUSE_TLC.x + 60, PAUSE_TOP, PAUSE_BRC.x - 20, PAUSE_BOTTOM, paint);
        }
        // Draw play button
        else {
            Point a = new Point(PAUSE_TLC.x + 30, PAUSE_TOP);
            Point b = new Point(PAUSE_TLC.x + 30, PAUSE_BOTTOM);
            Point c = new Point(PAUSE_BRC.x - 20, (PAUSE_BOTTOM + PAUSE_TOP) / 2);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(a.x, a.y);
            path.lineTo(b.x, b.y);
            path.lineTo(c.x, c.y);
            path.lineTo(a.x, a.y);
            path.close();

            canvas.drawPath(path, paint);
        }

        paint.setAlpha(255);
    }


    // Draws instructions for the player's first playthrough
    private void drawWelcome() {
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("Welcome space ranger!", screenWidth / 2, screenHeight / 2 - 250, paint);

        paint.setTextSize(40);
        canvas.drawText("Press the left or right buttons to move.", screenWidth / 2, screenHeight / 2 - 150, paint);
        canvas.drawText("Tap anywhere else on the screen to shoot! ", screenWidth / 2, screenHeight / 2 - 50, paint);

    }

    private void drawPowerUps() {
        for (SpaceObject powerup : powerups) {
            drawPlayer(powerup);
        }
    }

    private void drawGameOver() {
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Good grief!", screenWidth / 2, screenHeight / 2 - 250, paint);
        canvas.drawText("The galactic federation has taken over.", screenWidth / 2, screenHeight / 2 - 150, paint);
        canvas.drawText("Tap to replay!", screenWidth / 2, screenHeight / 2 - 50, paint);

    }

    private void drawPlayerWon() {

        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Congratulations!", screenWidth / 2, screenHeight / 2 - 250, paint);
        canvas.drawText("You beat the evil galactic federation", screenWidth / 2, screenHeight / 2 - 150, paint);
        canvas.drawText("Tap to replay!", screenWidth / 2, screenHeight / 2 - 50, paint);
    }

    private void drawBullets() {
        List<Bullet> bullets = player.getBullets();
        // Draw player bullets
        for (Bullet bullet : bullets) {
            paint.setColor(Color.BLUE);
            canvas.drawRect(bullet.getX(), bullet.getY(), bullet.getX() + bullet.getWidth(), bullet.getY() + bullet.getHeight(), paint);
        }
        // Draw enemy bullets
        for (Enemy enemy : enemies) {
            for (Bullet bullet : enemy.getBullets()) {
                paint.setARGB(255, 00, 159, 17);
                canvas.drawRect(bullet.getX(), bullet.getY(), bullet.getX() + bullet.getWidth(), bullet.getY() + bullet.getHeight(), paint);
            }
        }
    }

    private void drawHUD() {
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Lives:  " + player.getLives(), screenWidth - 50, 75, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Score:  " + player.score, 50, 75, paint);
    }

    private void drawButtons() {
        paint.setColor(Color.WHITE);
        paint.setAlpha(125);

        canvas.drawRoundRect(LEFT_BUTTON, 20, 20, paint);
        canvas.drawRoundRect(RIGHT_BUTTON, 20, 20, paint);
    }

    private void drawStars() {
        paint.setColor(Color.YELLOW);

        for (Star star : stars) {
            paint.setStrokeWidth(star.width);
            canvas.drawPoint(star.getX(), star.getY(), paint);
        }
    }

    private void drawPlayer(SpaceObject player) {
        paint.setARGB(255, 92, 13, 121);

        int squareSize = player.width / 5;

        // Ship core (large square in the middle)
        canvas.drawRect(player.getX() + squareSize, player.getY() + squareSize,
                player.getX() + squareSize * 4, player.getY() + squareSize * 4, paint);

        // Ship cannon (on top of ship core)
        canvas.drawRect(player.getX() + squareSize * 2, player.getY(),
                player.getX() + squareSize * 3, player.getY() + squareSize, paint);

        // Ship left front wing
        canvas.drawRect(player.getX(), player.getY() + squareSize * 2,
                player.getX() + squareSize, player.getY() + squareSize * 3, paint);

        // Ship right front wing
        canvas.drawRect(player.getX() + player.width - squareSize, player.getY() + squareSize * 2,
                player.getX() + player.width, player.getY() + squareSize * 3, paint);

        // Ship left back wing
        canvas.drawRect(player.getX(), player.getY() + player.height - squareSize,
                player.getX() + squareSize * 2, player.getY() + player.height, paint);

        // Ship right back wing
        canvas.drawRect(player.getX() + squareSize * 3, player.getY() + player.height - squareSize,
                player.getX() + player.width, player.getY() + player.height, paint);
    }

    private void drawEnemies() {
        paint.setColor(Color.RED);

        int squareSize = enemies.get(0).width / 5;

        for (Enemy enemy : enemies) {
            int x = enemy.getX();
            int y = enemy.getY();

            // Left rectangle of enemy (Left ear)
            canvas.drawRect(x, y, x + squareSize,
                    y + enemy.height - squareSize, paint);

            // Right rectangle of enemy (Right ear)
            canvas.drawRect(x + enemy.width - squareSize, y, x + enemy.width,
                    y + enemy.height - squareSize, paint);

            // Top horizontal rectangle
            canvas.drawRect(x + squareSize, y + squareSize,
                    x + enemy.width - squareSize, y + squareSize * 2, paint);

            // Bottom horizontal rectangle
            canvas.drawRect(x + squareSize, y + squareSize * 3,
                    x + enemy.width - squareSize, y + enemy.height - squareSize, paint);

            // Middle square
            canvas.drawRect(x + squareSize * 2, y + squareSize * 2,
                    x + squareSize * 3, y + squareSize * 3, paint);

            // Left fang
            canvas.drawRect(x + squareSize, y + enemy.height - squareSize,
                    x + squareSize * 2, y + enemy.height, paint);

            // Right fang
            canvas.drawRect(x + squareSize * 3, y + enemy.height - squareSize,
                    x + enemy.width - squareSize, y + enemy.height, paint);
        }
    }

    private void drawPauseMenu() {
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(80);
        canvas.drawText("Paused!", getWidth() / 2, getHeight() / 2, paint);
    }

    private void drawBarriers() {

        for (Barrier b : barriers) {
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(b.getX(), b.getY(), b.getX() + b.width, b.getY() + b.height, paint);

            // For debugging where each barrier is
//            paint.setColor(Color.BLACK);
//            paint.setStyle(Paint.Style.STROKE);
//            canvas.drawRect(b.getX(), b.getY(), b.getX() + b.width, b.getY() + b.height, paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void control() {
        try {
            thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        isRunning = false;
        draw();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                if (pressedPause(event.getX(), event.getY())) {
                    if (isRunning) {
                        pause();
                    } else {
                        resume();
                    }
                }
                if (!detectLeftRightButton(event.getX(), event.getY()) && !isGameOver) {
                    if (player.shoot(Bullet.UP)) {
                        SoundPlayer.instance(context).play(SoundPlayer.Sound.PLAYER_SHOOT);
                    }
                } else if (isGameOver && firstTimePlaying) {
                    isGameOver = false;
                    firstTimePlaying = false;
                } else if (isGameOver) {
                    start();
                }
                break;
            case MotionEvent.ACTION_UP:
                player.stopMoving();
                break;
        }
        return true;
    }

    /**
     * Detects whether a player tapped the left or right movement
     * buttons and moves the player ship accordingly.
     *
     * @param x - x-coordinate of press
     * @param y - y-coordinate of press
     * @return boolean - True if button was pressed, False otherwise
     */
    private boolean detectLeftRightButton(float x, float y) {
        // Check if y is in range of buttons
        if (y >= LEFT_BUTTON.top && y <= LEFT_BUTTON.bottom) {
            // Check for left button click
            if (x >= LEFT_BUTTON.left && x <= LEFT_BUTTON.right) {
                player.moveLeft();
                return true;
            }
            // Check for right button click
            else if (x >= RIGHT_BUTTON.left && x <= RIGHT_BUTTON.right) {
                player.moveRight();
                return true;
            }
        }
        return false;
    }

    private boolean pressedPause(float x, float y) {
        return (x >= PAUSE_TLC.x && x <= PAUSE_BRC.x) && (y >= PAUSE_TLC.y && y <= PAUSE_BRC.y);
    }

}
