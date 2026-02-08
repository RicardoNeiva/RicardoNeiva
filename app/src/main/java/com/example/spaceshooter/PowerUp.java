package com.example.spaceshooter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class PowerUp {
    public static final int TYPE_SHIELD = 0;
    public static final int TYPE_RAPID_FIRE = 1;

    private int x, y;
    private int type;
    private int width = 50;
    private int height = 50;
    private int speed = 10;
    private boolean active = true;
    private Paint paint;
    private int screenHeight;

    public PowerUp(int x, int y, int type, int screenHeight) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.screenHeight = screenHeight;
        paint = new Paint();
        if (type == TYPE_SHIELD) {
            paint.setColor(Color.BLUE);
        } else {
            paint.setColor(Color.MAGENTA);
        }
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

    public int getType() {
        return type;
    }
}
