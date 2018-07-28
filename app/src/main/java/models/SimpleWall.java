package models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

import constants.paramConst;
import maker.Geometric_Calculate;
import wordcreators.fingerball.GameView;
import wordcreators.fingerball.MainActivity;


public class SimpleWall extends Objects {

    boolean isExist = true;

    private boolean isFigure = false;

    private Objects figure;

    private float speed = 0;
    private double angle = 0;

    int color;

    private boolean delete = false;
    private boolean firstDraw = true;
    private float lineToFire[];

    public SimpleWall(float x, float y, float spriteH, float spriteW, GameView gameView, boolean isFigure, Objects figure, float speed, double angle, boolean isExist) {
        super(x, y, spriteH, spriteW, gameView);

        this.isFigure = isFigure;

        this.figure = figure;

        this.speed = speed;
        this.angle = angle;

        this.isExist = isExist;

        Random random = new Random();
        color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    public boolean getIsFigure() {
        return isFigure;
    }

    public Objects getFigure() {
        return figure;
    }

    public void setStartCoord(float startX, float startY) {
        setX(startX);
        setY(startY);
    }

    public void setEndCoord(float endX, float endY) {
        setSpriteW(endX);
        setSpriteH(endY);
    }

    @Override
    public void draw(Paint paint, Canvas canvas) {

        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(gameView.getQwadWidth() / 4);
        if (!isExist)
            paint.setAlpha(50);
        if (isExist && gameView.IsRainbow())
            paint.setColor(color);
        canvas.drawLine(getX(), getY(), getSpriteW(), getSpriteH(), paint);
        if (!isExist) {
            gameView.gameWindow.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                //lineToFire = Geometric_Calculate.getTrueLineFire(getX(), getY(), getSpriteW(), getSpriteH(),
                 //       gameView.getBall().getX() + gameView.getBall().getSpriteW() / 2, gameView.getBall().getY() + gameView.getBall().getSpriteH() / 2);
                //float startX = getX();
                //float startY = getY();
                //float destX = Geometric_Calculate.destination(lineToFire[0], 0, lineToFire[2], 0);
                //float destY = Geometric_Calculate.destination(0, lineToFire[1], 0, lineToFire[3]);
                //float size = gameView.getQwadHeight() + gameView.getQwadWidth();
                //Random random = new Random();
                //float check1 = getSpriteW(), check2 = getSpriteH();
                //int count = 3;
                //while (count > 0) {
                    //gameView.addObjectFigure(new SimpleFigure(getSpriteW(), getSpriteH(), 0, 0, gameView, paramConst.FIRE_FIG, size / 2, 0,
                     //       (float) random.nextInt(360) * Math.PI / 180, 20));
                    //count--;
                //}
            }
        });
        }
        /*if (firstDraw) {
            firstDraw = false;
            //gameView.gameWindow.runOnUiThread(new Runnable() {
                //@Override
                //public void run() {
                    lineToFire = Geometric_Calculate.getTrueLineFire(getX(), getY(), getSpriteW(), getSpriteH(),
                            gameView.getBall().getX() + gameView.getBall().getSpriteW() / 2, gameView.getBall().getY() + gameView.getBall().getSpriteH() / 2);
                    float startX = getX();
                    float startY = getY();
                    float destX = Geometric_Calculate.destination(lineToFire[0], 0, lineToFire[2], 0);
                    float destY = Geometric_Calculate.destination(0, lineToFire[1], 0, lineToFire[3]);
                    float size = gameView.getQwadHeight() + gameView.getQwadWidth();
                    Random random = new Random();
                    float check1 = getSpriteW(), check2 = getSpriteH();
                    while (Geometric_Calculate.destination(startX, 0, getX(), 0) < destX && Geometric_Calculate.destination(0, startY, 0, getY()) < destY) {
                        gameView.addObjectFigure(new SimpleFigure(startX, startY, 0, 0, gameView, paramConst.FIRE_FIG, size, speed,
                                Geometric_Calculate.getAnglePoint(random.nextInt((int) destX) + lineToFire[0], random.nextInt((int) destY) + lineToFire[1],
                                        gameView.getBall().getX() + gameView.getBall().getSpriteW() / 2, gameView.getBall().getY() + gameView.getBall().getSpriteH() / 2), paramConst.START_SPEED_LIFE_FIG));
                        if (Geometric_Calculate.destination(startX, 0, getX(), 0) < destX)
                            startX += size;
                        if (Geometric_Calculate.destination(0, startY, 0, getY()) < destY)
                            startY += size;
                    }
                }
            //});

        //}*/
        paint.setAlpha(255);
    }

    @Override
    public boolean update(long gameTime) {
        if (speed != 0) {
            float newX = getX() + speed * (float) Math.cos(angle);
            float newY = getY() + speed * (float) Math.sin(angle);

            if ((spriteW > 0 && spriteW < gameView.getWidthMap()) && (spriteH > 0 && spriteH < gameView.getHeightMap()) && !delete)
                delete = true;

            spriteW = spriteW + speed * (float) Math.cos(angle);
            spriteH = spriteH + speed * (float) Math.sin(angle);

            setX_FULL(newX);
            setY_FULL(newY);

            if (((getX() <= 0 || getX() >= gameView.getWidthMap()) || (getY() <= 0 || getY() >= gameView.getHeightMap())) && delete) {
                int i;
                i = 5;
                return true;
            }
        }
        return false;
    }
}
