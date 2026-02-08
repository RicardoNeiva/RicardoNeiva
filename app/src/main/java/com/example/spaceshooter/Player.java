package com.example.spaceshooter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Player {
    private int x, y;
    private int width = 100;
    private int height = 100;
    private Paint paint;
    private int screenWidth, screenHeight;

    private boolean isShieldActive = false;
    private boolean isRapidFireActive = false;
    private long shieldTimer = 0;
    private long rapidFireTimer = 0;
    private static final long POWERUP_DURATION = 5000; // 5 seconds

    public Player(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.x = screenWidth / 2 - width / 2;
        this.y = screenHeight - height - 100;

        paint = new Paint();
        paint.setColor(Color.CYAN);
    }

    public void update(int touchX) {
        if (touchX != -1) {
            x = touchX - width / 2;
        }

        // Clamp to screen
        if (x < 0) x = 0;
        if (x > screenWidth - width) x = screenWidth - width;

        long currentTime = System.currentTimeMillis();
        if (isShieldActive && currentTime > shieldTimer) {
            isShieldActive = false;
        }
        if (isRapidFireActive && currentTime > rapidFireTimer) {
            isRapidFireActive = false;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(getRect(), paint);

        if (isShieldActive) {
            Paint shieldPaint = new Paint();
            shieldPaint.setColor(Color.argb(100, 0, 0, 255));
            shieldPaint.setStyle(Paint.Style.STROKE);
            shieldPaint.setStrokeWidth(10);
            canvas.drawCircle(x + width / 2, y + height / 2, width, shieldPaint);
        }
    }

    public Rect getRect() {
        return new Rect(x, y, x + width, y + height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }

    public void activateShield() {
        isShieldActive = true;
        shieldTimer = System.currentTimeMillis() + POWERUP_DURATION;
    }

    public void activateRapidFire() {
        isRapidFireActive = true;
        rapidFireTimer = System.currentTimeMillis() + POWERUP_DURATION;
    }

    public boolean isShieldActive() {
        return isShieldActive;
    }

    public boolean isRapidFireActive() {
        return isRapidFireActive;
    }
}
