package models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

import wordcreators.fingerball.GameView;
import wordcreators.fingerball.MainActivity;

/**
 * Created by anton on 05.01.2018.
 */

public class MoveWall extends Objects {
    private double angle = 0;
    private float speed = 5;
    private int size = 1;

    int color;

    private boolean delete = false;

    public MoveWall(float x, float y, float spriteH, float spriteW, GameView gameView, double angle, float speed, int size) {
        super(x, y, spriteH, spriteW, gameView);

        this.spriteW = x - size * (float) Math.cos(angle);
        this.spriteW = y - size * (float) Math.sin(angle);
        this.speed = speed;
        this.angle = angle;
        this.size = size;

        Random random = new Random();
        color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    @Override
    public void draw(Paint paint, Canvas canvas) {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(gameView.getQwadWidth() / 4);
        if (gameView.IsRainbow())
            paint.setColor(color);
        canvas.drawLine(getX(), getY(), getSpriteW(), getSpriteH(), paint);
    }

    @Override
    public boolean update(long gameTime) {
        float newX = getX() + speed * (float) Math.cos(angle);
        float newY = getY() + speed * (float) Math.sin(angle);

        if ((spriteW > 0 && spriteW < gameView.getWidthMap()) && (spriteH > 0 && spriteH < gameView.getHeightMap()) && !delete)
            delete = true;

        spriteW = newX - size * (float) Math.cos(angle);
        spriteH = newY - size * (float) Math.sin(angle);

        setX_FULL(newX);
        setY_FULL(newY);

        if (((spriteW <= 0 || spriteW >= gameView.getWidthMap()) || (spriteH <= 0 || spriteH >= gameView.getHeightMap())) && delete)
            return true;

        return false;
    }
}
