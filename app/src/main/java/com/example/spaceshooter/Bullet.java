package com.example.spaceshooter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Bullet {
    private int x, y;
    private int width = 20;
    private int height = 40;
    private int speed = 40;
    private Paint paint;
    private boolean active = true;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
        paint = new Paint();
        paint.setColor(Color.YELLOW);
    }

    public void update() {
        y -= speed;
        if (y < -height) {
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
