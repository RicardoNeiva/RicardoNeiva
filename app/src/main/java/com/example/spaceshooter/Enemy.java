package com.example.spaceshooter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.Random;

public class Enemy {
    private int x, y;
    private int width = 100;
    private int height = 100;
    private int speed = 15;
    private Paint paint;
    private int screenWidth, screenHeight;
    private boolean active = true;

    public Enemy(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        Random random = new Random();
        this.x = random.nextInt(screenWidth - width);
        this.y = -height;

        paint = new Paint();
        paint.setColor(Color.RED);
    }

    public void update() {
        y += speed;
        if (y > screenHeight) {
            active = false;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(getRect(), paint);
    }

    public Rect getRect() {
        return new Rect(x, y, x + width, y + height);
    }

    public boolean isActive() {
        return active;
    }
}
