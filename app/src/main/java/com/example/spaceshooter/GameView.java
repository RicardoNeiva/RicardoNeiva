package com.example.spaceshooter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private enum GameState {
        MENU, PLAYING, GAME_OVER
    }

    private Thread gameThread;
    private volatile boolean isPlaying;
    private Player player;
    private List<Bullet> bullets;
    private List<Enemy> enemies;
    private List<PowerUp> powerUps;
    private List<Explosion> explosions;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private int screenX, screenY;
    private int score = 0;
    private int highScore = 0;
    private GameState gameState = GameState.MENU;
    private int enemySpawnTimer = 0;
    private int bulletSpawnTimer = 0;
    private int level = 1;
    private int spawnRate = 40;
    private Random random = new Random();
    private SharedPreferences prefs;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        this.screenX = screenX;
        this.screenY = screenY;
        surfaceHolder = getHolder();
        paint = new Paint();

        prefs = context.getSharedPreferences("SpaceShooter", Context.MODE_PRIVATE);
        highScore = prefs.getInt("HighScore", 0);

        player = new Player(screenX, screenY);
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        powerUps = new ArrayList<>();
        explosions = new ArrayList<>();
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        if (gameState != GameState.PLAYING) return;

        // Level Logic
        if (score > level * 500) {
            level++;
            spawnRate = Math.max(10, 40 - (level * 2)); // Increase spawn rate
        }

        // Auto fire
        bulletSpawnTimer++;
        if (player.isRapidFireActive()) {
            if (bulletSpawnTimer > 5) { // Rapid fire
                bullets.add(new Bullet(player.getX() + player.getWidth() / 2 - 10, player.getY()));
                bulletSpawnTimer = 0;
            }
        } else {
            if (bulletSpawnTimer > 15) { // Normal fire
                bullets.add(new Bullet(player.getX() + player.getWidth() / 2 - 10, player.getY()));
                bulletSpawnTimer = 0;
            }
        }

        // Spawn enemies
        enemySpawnTimer++;
        if (enemySpawnTimer > spawnRate) {
            enemies.add(new Enemy(screenX, screenY));
            enemySpawnTimer = 0;
        }

        List<Bullet> bulletsToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();
        List<PowerUp> powerUpsToRemove = new ArrayList<>();
        List<Explosion> explosionsToRemove = new ArrayList<>();

        // Update bullets
        for (Bullet bullet : bullets) {
            bullet.update();
            if (!bullet.isActive()) {
                bulletsToRemove.add(bullet);
            }
        }

        // Update enemies and check collisions
        for (Enemy enemy : enemies) {
            enemy.update(); // TODO: Pass level for speed increase
            if (!enemy.isActive()) {
                enemiesToRemove.add(enemy);
                continue;
            }

            if (Rect.intersects(enemy.getRect(), player.getRect())) {
                if (player.isShieldActive()) {
                    enemiesToRemove.add(enemy); // Shield destroys enemy
                    explosions.add(new Explosion(enemy.getRect().centerX(), enemy.getRect().centerY(), 20, Color.RED));
                } else {
                    gameOver();
                }
            }

            for (Bullet bullet : bullets) {
                if (!bulletsToRemove.contains(bullet) && Rect.intersects(enemy.getRect(), bullet.getRect())) {
                    bulletsToRemove.add(bullet);
                    enemiesToRemove.add(enemy);
                    explosions.add(new Explosion(enemy.getRect().centerX(), enemy.getRect().centerY(), 20, Color.RED));
                    score += 10;

                    // Chance to spawn PowerUp
                    if (random.nextInt(100) < 10) { // 10% chance
                        int type = random.nextInt(2);
                        powerUps.add(new PowerUp(enemy.getRect().centerX(), enemy.getRect().centerY(), type, screenY));
                    }
                    break;
                }
            }
        }

        // Update PowerUps
        for (PowerUp powerUp : powerUps) {
            powerUp.update();
            if (!powerUp.isActive()) {
                powerUpsToRemove.add(powerUp);
            }

            if (Rect.intersects(powerUp.getRect(), player.getRect())) {
                if (powerUp.getType() == PowerUp.TYPE_SHIELD) {
                    player.activateShield();
                } else {
                    player.activateRapidFire();
                }
                powerUpsToRemove.add(powerUp);
            }
        }

        // Update explosions
        for (Explosion explosion : explosions) {
            explosion.update();
            if (!explosion.isActive()) {
                explosionsToRemove.add(explosion);
            }
        }

        bullets.removeAll(bulletsToRemove);
        enemies.removeAll(enemiesToRemove);
        powerUps.removeAll(powerUpsToRemove);
        explosions.removeAll(explosionsToRemove);
    }

    private void gameOver() {
        gameState = GameState.GAME_OVER;
        if (score > highScore) {
            highScore = score;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("HighScore", highScore);
            editor.apply();
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            if (gameState == GameState.MENU) {
                drawMenu();
            } else if (gameState == GameState.PLAYING) {
                drawGame();
            } else if (gameState == GameState.GAME_OVER) {
                drawGameOver();
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawMenu() {
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("SPACE SHOOTER", screenX / 2f, screenY / 2f - 100, paint);
        paint.setTextSize(60);
        canvas.drawText("High Score: " + highScore, screenX / 2f, screenY / 2f, paint);
        canvas.drawText("Tap to Start", screenX / 2f, screenY / 2f + 100, paint);
    }

    private void drawGame() {
        paint.setColor(Color.WHITE);

        player.draw(canvas);

        for (Bullet bullet : bullets) {
            bullet.draw(canvas);
        }

        for (Enemy enemy : enemies) {
            enemy.draw(canvas);
        }

        for (PowerUp powerUp : powerUps) {
            powerUp.draw(canvas);
        }

        for (Explosion explosion : explosions) {
            explosion.draw(canvas);
        }

        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Score: " + score, 50, 100, paint);
        canvas.drawText("Level: " + level, screenX - 300, 100, paint);

        if (player.isShieldActive()) {
             paint.setColor(Color.BLUE);
             canvas.drawText("SHIELD", 50, 200, paint);
        }
        if (player.isRapidFireActive()) {
             paint.setColor(Color.MAGENTA);
             canvas.drawText("RAPID FIRE", 50, 300, paint);
        }
    }

    private void drawGameOver() {
        // Draw the game state in background slightly dimmed? For now, just black screen + text
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("GAME OVER", screenX / 2f, screenY / 2f - 100, paint);
        paint.setTextSize(60);
        canvas.drawText("Score: " + score, screenX / 2f, screenY / 2f, paint);
        canvas.drawText("High Score: " + highScore, screenX / 2f, screenY / 2f + 100, paint);
        canvas.drawText("Tap to Restart", screenX / 2f, screenY / 2f + 250, paint);
    }

    private void control() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (gameState == GameState.MENU) {
                    gameState = GameState.PLAYING;
                } else if (gameState == GameState.GAME_OVER) {
                    restart();
                }
                if (gameState == GameState.PLAYING) {
                    player.update((int) event.getX());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (gameState == GameState.PLAYING) {
                    player.update((int) event.getX());
                }
                break;
        }
        return true;
    }

    private void restart() {
        gameState = GameState.PLAYING;
        score = 0;
        level = 1;
        spawnRate = 40;
        bullets.clear();
        enemies.clear();
        powerUps.clear();
        explosions.clear();
        player = new Player(screenX, screenY);
    }
}
