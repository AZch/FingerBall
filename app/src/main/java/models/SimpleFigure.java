package models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

import constants.paramConst;
import maker.Geometric_Calculate;
import wordcreators.fingerball.GameView;
import wordcreators.fingerball.MainActivity;

public class SimpleFigure extends Objects {
    private ArrayList<Objects> walls = new ArrayList<>();

    private boolean existFig = true;

    private int thisCode = 0;

    private int countOnePlace = 100 + 255;

    private int speedLife = paramConst.START_SPEED_LIFE_FIG;

    private int countAlpha = 0;

    int color;

    float size;

    private boolean isActiveFig = false;

    private float speed = 0;
    private double angle = 0;

    public SimpleFigure(float x, float y, float spriteH, float spriteW, GameView gameView, int whatFigure, float size, float speed, double angle, int speedLife) {
        super(x, y, spriteH, spriteW, gameView);

        Random random = new Random();
        color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));

        thisCode = whatFigure;

        this.size = size;

        setFigure(size, whatFigure);

        this.speed = speed;
        this.angle = angle;

        this.speedLife = speedLife;
    }

    private void setFigure(float size, int code) {
        switch (code) {
            case paramConst.SQUARE_FIG:
                setSquare(size);
                break;
            case paramConst.FOUR_POINTER_STAR:
                setFourStar(size);
                break;
            case paramConst.FIVE_POINTER_STAR:
                setFiveStar(size);
                break;
            case paramConst.PENTAGON:
                setPentagon(size);
                break;
            case paramConst.TRIANGLE:
                setTraingle(size);
                break;
            case paramConst.FIRE_FIG:
                setFire(size);
                break;
            case paramConst.RECTANGLE_FIG:
                setRectangle(size);
                break;
            default:
                existFig = false;
                break;
        }
    }

    public float getSpeed() {
        return speed;
    }

    private void setFire(float size) {
        walls.add(new SimpleWall(getX() + size / 2, getY(), getY() + size, getX() + size / 2, gameView, true, this, speed, angle, false));
        walls.add(new SimpleWall(getX(), getY() + size / 2, getY() + size / 2, getX() + size, gameView, true, this, speed, angle, false));
        walls.add(new SimpleWall(getX() + size, getY(), getY() + size, getX(), gameView, true, this, speed, angle, false));
        walls.add(new SimpleWall(getX(), getY(), getY() + size, getX() + size, gameView, true, this, speed, angle, false));
    }

    private void setSquare(float size) {
        walls.add(new SimpleWall(getX(), getY(), getY() + size, getX(), gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX(), getY(), getY(), getX() + size, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX() + size, getY(), getY() + size, getX() + size, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX(), getY() + size, getY() + size, getX() + size, gameView, true, this, speed, angle, true));
    }

    private void setRectangle(float size) {
        walls.add(new SimpleWall(getX(), getY(), getSpriteH(), getX(), gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX(), getY(), getY(), getSpriteW(), gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getSpriteW(), getY(), getSpriteH(), getSpriteW(), gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX(), getSpriteH(), getSpriteH(), getSpriteW(), gameView, true, this, speed, angle, true));
    }

    private void setFourStar(float size) {
        walls.add(new SimpleWall(getX() + size / 4, getY(), getY(), getX() + 3 * size / 4, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX() + 3 * size / 4, getY(), getY() + size / 2, getX() + size, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX() + size, getY() + size / 2, getY() + size, getX() + 3 * size / 4, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX() + 3 * size / 4, getY() + size, getY() + size, getX() + size / 4, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX() + size / 4, getY() + size, getY() + size / 2, getX(), gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX(), getY() + size / 2, getY(), getX() + size / 4, gameView, true, this, speed, angle, true));

    }

    private void setFiveStar(float size) {
        walls.add(new SimpleWall(getX() + size / 2, getY(), getY() + size, getX() + 4 * size / 5, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX() + 4 * size / 5, getY() + size, getY() + size / 2, getX(), gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX(), getY() + size / 2, getY() + size / 2, getX() + size, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX() + size, getY() + size / 2, getY() + size, getX() + size / 5, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX() + size / 5, getY() + size, getY(), getX() + size / 2, gameView, true, this, speed, angle, true));
    }

    private void setPentagon(float size) {
        walls.add(new SimpleWall(getX() + size / 2, getY(), getY() + size / 2, getX() + size, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX() + size, getY() + size / 2, getY() + size, getX() + 3 * size / 4, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX() + 3 * size / 4, getY() + size, getY() + size, getX() + size / 4, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX() + size / 4, getY() + size, getY() + size / 2, getX(), gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX(), getY() + size / 2, getY(), getX() + size / 2, gameView, true, this, speed, angle, true));
    }

    private void setTraingle(float size) {
        walls.add(new SimpleWall(getX() + size / 2, getY(), getY() + size, getX() + size, gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX() + size, getY() + size, getY() + size, getX(), gameView, true, this, speed, angle, true));
        walls.add(new SimpleWall(getX(), getY() + size, getY(), getX() + size / 2, gameView, true, this, speed, angle, true));
    }

    public ArrayList<Objects> getWalls() {return walls;}

    @Override
    public void draw(Paint paint, Canvas canvas) {
        paint.setColor(Color.GRAY);
        if (gameView.IsRainbow())
            paint.setColor(color);
        if (countAlpha < 170 && countOnePlace > 0) {
            paint.setAlpha(countAlpha);
            countAlpha += speedLife;
            if (170 - countAlpha < 30)
                isActiveFig = true;
        }

        if (countOnePlace <= 0){
            paint.setAlpha(countAlpha);
            countAlpha -= speedLife;
            if (countAlpha < 0)
                countAlpha = 0;
            if (countAlpha < 40)
                isActiveFig = false;
        }

        paint.setStrokeWidth(gameView.getQwadWidth() / 4);
        if (thisCode == paramConst.RECTANGLE_FIG)
            paint.setStrokeWidth(gameView.getQwadWidth() / 2);
        for (Objects obj : walls) {
            canvas.drawLine(obj.getX(), obj.getY(), obj.getSpriteW(), obj.getSpriteH(), paint);
        }
        paint.setAlpha(255);
    }

    public boolean getIsActiveFig() {
        return  isActiveFig;
    }

    @Override
    public boolean update(long gameTime) {
        for (Objects wall : walls) {
            wall.setX(wall.getX() + speed * (float) Math.cos(angle));
            wall.setY(wall.getY() + speed * (float) Math.sin(angle));

            wall.spriteW = wall.spriteW + speed * (float) Math.cos(angle);
            wall.spriteH = wall.spriteH + speed * (float) Math.sin(angle);
        }

        if (countAlpha >= 170)
            isActiveFig = true;
        if (countOnePlace <= 0 && countAlpha <= 0) {
            return true;
        } else
            countOnePlace--;

        //if (walls.get(0).getX() < 0 || walls.get(0).getX() > gameView.getWidthMap() || walls.get(0).getY() < 0 || walls.get(0).getY() > gameView.getHeightMap())
         //   return true;

        if (!existFig)
            return true;
        return false;
    }
}
