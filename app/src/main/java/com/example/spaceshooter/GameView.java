package com.example.spaceshooter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private volatile boolean isPlaying;
    private Player player;
    private List<Bullet> bullets;
    private List<Enemy> enemies;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private int screenX, screenY;
    private int score = 0;
    private boolean isGameOver = false;
    private int enemySpawnTimer = 0;
    private int bulletSpawnTimer = 0;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        this.screenX = screenX;
        this.screenY = screenY;
        surfaceHolder = getHolder();
        paint = new Paint();

        player = new Player(screenX, screenY);
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
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
        if (isGameOver) return;

        // Auto fire
        bulletSpawnTimer++;
        if (bulletSpawnTimer > 15) {
            bullets.add(new Bullet(player.getX() + player.getWidth() / 2 - 10, player.getY()));
            bulletSpawnTimer = 0;
        }

        // Spawn enemies
        enemySpawnTimer++;
        if (enemySpawnTimer > 40) {
            enemies.add(new Enemy(screenX, screenY));
            enemySpawnTimer = 0;
        }

        List<Bullet> bulletsToRemove = new ArrayList<>();
        List<Enemy> enemiesToRemove = new ArrayList<>();

        // Update bullets
        for (Bullet bullet : bullets) {
            bullet.update();
            if (!bullet.isActive()) {
                bulletsToRemove.add(bullet);
            }
        }

        // Update enemies and check collisions
        for (Enemy enemy : enemies) {
            enemy.update();
            if (!enemy.isActive()) {
                enemiesToRemove.add(enemy);
                continue;
            }

            if (Rect.intersects(enemy.getRect(), player.getRect())) {
                isGameOver = true;
            }

            for (Bullet bullet : bullets) {
                if (!bulletsToRemove.contains(bullet) && Rect.intersects(enemy.getRect(), bullet.getRect())) {
                    bulletsToRemove.add(bullet);
                    enemiesToRemove.add(enemy);
                    score += 10;
                    break;
                }
            }
        }

        bullets.removeAll(bulletsToRemove);
        enemies.removeAll(enemiesToRemove);
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            paint.setColor(Color.WHITE);
            // Draw stars? Maybe later.

            player.draw(canvas);

            for (Bullet bullet : bullets) {
                bullet.draw(canvas);
            }

            for (Enemy enemy : enemies) {
                enemy.draw(canvas);
            }

            paint.setColor(Color.WHITE);
            paint.setTextSize(60);
            canvas.drawText("Score: " + score, 50, 100, paint);

            if (isGameOver) {
                paint.setTextSize(100);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("GAME OVER", screenX / 2f, screenY / 2f, paint);
                paint.setTextSize(60);
                canvas.drawText("Tap to Restart", screenX / 2f, screenY / 2f + 100, paint);
                paint.setTextAlign(Paint.Align.LEFT); // Reset
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
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
                if (isGameOver) {
                    restart();
                }
                player.update((int) event.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isGameOver) {
                    player.update((int) event.getX());
                }
                break;
        }
        return true;
    }

    private void restart() {
        isGameOver = false;
        score = 0;
        bullets.clear();
        enemies.clear();
        player = new Player(screenX, screenY);
    }
}
