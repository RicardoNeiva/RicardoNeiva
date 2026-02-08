package com.example.spaceshooter;

import android.graphics.Canvas;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.List;

public class Explosion {
    private List<Particle> particles;
    private boolean active = true;

    public Explosion(float x, float y, int particleCount, int color) {
        particles = new ArrayList<>();
        for (int i = 0; i < particleCount; i++) {
            particles.add(new Particle(x, y, color));
        }
    }

    public void update() {
        boolean anyAlive = false;
        for (Particle p : particles) {
            p.update();
            if (p.isAlive()) {
                anyAlive = true;
            }
        }
        if (!anyAlive) {
            active = false;
        }
    }

    public void draw(Canvas canvas) {
        for (Particle p : particles) {
            if (p.isAlive()) {
                p.draw(canvas);
            }
        }
    }

    public boolean isActive() {
        return active;
    }
}
