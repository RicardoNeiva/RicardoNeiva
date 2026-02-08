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
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(getRect(), paint);
    }

    public Rect getRect() {
        return new Rect(x, y, x + width, y + height);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
}
