package edu.utep.cs.cs4381.rocketshooter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
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
import edu.utep.cs.cs4381.rocketshooter.model.PlayerShip;
import edu.utep.cs.cs4381.rocketshooter.model.Star;

public class RSView extends SurfaceView implements Runnable {

    Context context;

    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;

    private boolean isRunning = true;
    private boolean isGameOver = false;

    private int screenWidth, screenHeight;

    private Thread thread;

    /// Entities in the game
    private PlayerShip player;
    private List<Enemy> enemies;
    private List<Barrier> barriers;
    private List<Star> stars;

    private RectF LEFT_BUTTON;
    private RectF RIGHT_BUTTON;

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

        LEFT_BUTTON = new RectF(10, screenHeight - 250, 300, screenHeight - 80);
        RIGHT_BUTTON = new RectF(screenWidth - 290, screenHeight - 250, screenWidth - 10, screenHeight - 80);

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


        player = new PlayerShip(screenWidth / 2 - 100, (int) LEFT_BUTTON.top - 50, screenWidth, screenHeight);

        //Create enemies
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                enemies.add(new Enemy(colIndexToScreen(col), rowIndexToScreen(row), screenWidth, screenHeight));
            }
        }

        //Create stars
        for (int i = 0; i < 60; i++) {
            int speed = random.nextInt(10);
            speed = speed == 0 ? 2 : speed;
            stars.add(new Star(3, screenWidth, screenHeight, speed));
        }

        // Create barriers
        for (int group = 0; group < 3; group++) {
            for (int col = 0; col < 4; col++) {
                for (int row = 0; row < 4; row++) {

                    int x = barrierIndexColToScreen(group, col);
                    int y = barrierIndexRowToScreen(row);
                    int width = (screenWidth / 3) / 4;
                    int height = width;

                    barriers.add(new Barrier(x, y, screenWidth, screenHeight, width, height));

                }
            }
        }
    }

    /**
     * Returns the x position of the barrier depending on which
     * [group] and what [index] it is in
     *
     * @param group
     * @param index
     * @return
     */
    private int barrierIndexColToScreen(int group, int index) {
        int GROUP_WIDTH = screenWidth / 3;
        return group * GROUP_WIDTH + index * (GROUP_WIDTH / 4);
    }

    /**
     * Returns the y position of the barrier depending on which
     * [group] and what [index] it is in
     *
     * @param index
     * @return
     */
    private int barrierIndexRowToScreen(int index) {
        return index * ((screenWidth / 3) / 4) + screenHeight / 2;
    }

    private int colIndexToScreen(int index) {
        return index * 250 + 75;
    }

    private int rowIndexToScreen(int index) {
        return index * 150 + 100;
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
        boolean isHit = false;

        if (!isGameOver) {
            player.update();
            for (Star star : stars) {
                star.update();
            }
            for (Enemy enemy : enemies) {
                enemy.update();
            }
            for (Barrier barrier : barriers) {
                barrier.update();
            }

            // Check if player hit barrier or enemy
            for (Bullet bullet : player.getBullets()) {
                for (Barrier barrier : barriers) {
                    if (bullet.getHitbox().intersects(barrier.getHitbox())) {
                        bullet.hideBullet();
                        barrier.setActive(false);
                    }
                }
                for (Enemy enemy : enemies) {
                    if (bullet.getHitbox().intersects(enemy.getHitbox())) {
                        bullet.hideBullet();
                        enemy.setActive(false);
                    }
                }

            }

            // Check if enemy hit barrier or enemy
            for (Enemy enemy : enemies) {
                for (Bullet bullet : enemy.getBullets()) {
                    for (Barrier barrier : barriers) {
                        if (bullet.getHitbox().intersects(barrier.getHitbox())) {
                            barrier.setActive(false);
                            bullet.hideBullet();
                        }
                    }
                    if (bullet.getHitbox().intersects(player.getHitbox())) {
                        player.takeLife();
                    }
                }
            }
        }
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
            drawPlayer();

            //Draw enemies
            drawEnemies();

            //Draw barriers
            drawBarriers();

            drawButtons();

            drawHUD();

            drawBullets();

            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawBullets() {
        List<Bullet> bullets = player.getBullets();
        for (Bullet bullet : bullets) {
            paint.setColor(Color.BLUE);
            canvas.drawRect(bullet.getX(), bullet.getY(), bullet.getX() + 10, bullet.getY() + 10, paint);
        }
    }

    private void drawHUD() {
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Lives:  " + player.getLives(), screenWidth - 50, 75, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Score:  0", 50, 75, paint);
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

    private void drawPlayer() {
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

        for (Enemy enemy : enemies) {
            canvas.drawRect(enemy.getX(), enemy.getY(), enemy.getX() + enemy.width, enemy.getY() + enemy.height, paint);
        }
    }

    private void drawBarriers() {

        for (Barrier b : barriers) {
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(b.getX(), b.getY(), b.getX() + b.width, b.getY() + b.height, paint);

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(b.getX(), b.getY(), b.getX() + b.width, b.getY() + b.height, paint);
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
//                detectLeftRightButton(event.getX(), event.getY());
                if (!detectLeftRightButton(event.getX(), event.getY())) {
                    player.shoot(Bullet.UP);
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


}
