package com.example.spaceshooter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint; // Added import
import java.util.Random;

public class Particle {
    private float x, y;
    private float vx, vy;
    private int color;
    private int life = 30; // Frames to live
    private Paint paint;
    private Random random = new Random();

    public Particle(float x, float y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;

        // Random velocity
        vx = (random.nextFloat() - 0.5f) * 20;
        vy = (random.nextFloat() - 0.5f) * 20;

        paint = new Paint();
        paint.setColor(color);
    }

    public void update() {
        x += vx;
        y += vy;
        life--;
    }

    public void draw(Canvas canvas) {
        if (life > 0) {
           paint.setAlpha((int)(life * 255.0f / 30.0f));
           canvas.drawCircle(x, y, 10, paint);
        }
    }

    public boolean isAlive() {
        return life > 0;
    }
}
