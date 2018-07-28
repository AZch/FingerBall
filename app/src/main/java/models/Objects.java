package models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;


import java.util.ArrayList;

import constants.paramConst;
import maker.Geometric_Calculate;
import wordcreators.fingerball.GameView;

/**
 * Объект на карте
 */
public abstract class Objects {

    // Координаты объекта
    private float x, y;

    protected  float spriteW, spriteH;

    GameView gameView;

    // текстура и id объекта на карте
    protected Bitmap bitmap; // текстура
    protected int bitMapId; // id

    // время обновления поледнего кадра
    protected long frameTicker;

    // сколько миллисекунд должно пройти перед сменой кадра (1000 / fps)
    protected int framePeriod;

    public Objects(float x, float y, float spriteH, float spriteW, GameView gameView) {
        this.spriteH = spriteH;
        this.spriteW = spriteW;
        this.gameView = gameView;

        setX(x);
        setY(y);

        framePeriod = 1000 / paramConst.FPS_ANIMATION_OBJECTS;
        frameTicker = 0;
    }

    // инициализация текстуры по id (стандартная)
    protected void init(Context context) {
        Bitmap contextBitMap = BitmapFactory.decodeResource(context.getResources(), bitMapId);
        bitmap = Bitmap.createScaledBitmap(
                contextBitMap, Math.round(spriteW /* * gameView.getZoomWidth()*/), Math.round(spriteH /* * gameView.getZoomHeight()*/), false
        );
        contextBitMap.recycle();
    }

    protected float getX () { return x; }
    protected float getY() { return y; }

    protected float getSpriteW() { return spriteW; }
    protected float getSpriteH() { return spriteH; }

    // задание координаты х объекта
    protected boolean setX(float x) {
        if (x <= 0) {
            this.x = x;
            return false;
        }
        else if (x >= gameView.getWidthMap()) {
            this.x = x;
            return false;
        }
        else {
            this.x = x;
            return true;
        }
    }

    // задание координаты у объекта
    protected boolean setY(float y) {

        if (y < 0) {
            this.y = y;
            return false;
        }
        else if (y >= gameView.getHeightMap()) {
            this.y = y;
            return false;
        }
        else {
            this.y = y;
            return true;
        }
    }

    protected void setSpriteW(float spriteW) {
        this.spriteW = spriteW;
    }

    protected void setSpriteH(float spriteH) {
        this.spriteH = spriteH;
    }

    protected boolean Move(double angle, float speed) {
        if (this instanceof Ball)
            ((Ball) this).angle = angle;

        boolean right = true, up = true, left = true, down = true;

        /*while (angle > Math.PI * 2)
            angle -= Math.PI * 2;
        while (angle < 0)
            angle += Math.PI * 2;
        if (angle >= 0 && angle <= Math.PI)
            down = false;
        if (angle >= Math.PI / 2 && angle <=  3 * Math.PI / 2)
            right = false;
        if (angle >= Math.PI && angle < Math.PI * 2)
            up = false;
        if ((angle >= 3 * Math.PI / 2 && angle < Math.PI * 2) || (angle >= 0 && angle <= Math.PI / 2))
            left = false;*/

        float newX1 = getX() + getSpriteW() / 2 + speed * (float) Math.cos(angle),
              newY1 = getY() + speed * (float) Math.sin(angle),
              newX2 = getX() + getSpriteW() + speed * (float) Math.cos(angle),
              newY2 = getY() + getSpriteH() / 2 + speed * (float) Math.sin(angle),
              newX3 = getX() + getSpriteW() / 2 + speed * (float) Math.cos(angle),
              newY3 = getY() + getSpriteH() + speed * (float) Math.sin(angle),
              newX4 = getX() + speed * (float) Math.cos(angle),
              newY4 = getY() + getSpriteH() / 2 + speed * (float) Math.sin(angle);

        ArrayList<Objects> objWall = gameView.getObjWall();

        try {
            for (Objects obj : objWall) {
                if (up && Geometric_Calculate.interesectionLinesBool(getX() + getSpriteW() / 2, getY(), newX1, newY1, obj.getX(), obj.getY(), obj.getSpriteW(), obj.getSpriteH())) {
                    gameView.destoyWall(obj, true);
                    return collision(getX() + getSpriteW() / 2, getY(), newX1, newY1, obj);
                } else  if (right && Geometric_Calculate.interesectionLinesBool(getX() + getSpriteW(), getY() + getSpriteH() / 2, newX2, newY2, obj.getX(), obj.getY(), obj.getSpriteW(), obj.getSpriteH())) {
                    gameView.destoyWall(obj, true);
                    return collision(getX() + getSpriteW(), getY() + getSpriteH() / 2, newX2, newY2, obj);
                } else  if (down && Geometric_Calculate.interesectionLinesBool(getX() + getSpriteW() / 2, getY() + getSpriteH(), newX3, newY3, obj.getX(), obj.getY(), obj.getSpriteW(), obj.getSpriteH())) {
                    gameView.destoyWall(obj, true);
                    return collision(getX() + getSpriteW() / 2,getY() + getSpriteH(),  newX3, newY3, obj);
                } else  if (left && Geometric_Calculate.interesectionLinesBool(getX(), getY() + getSpriteH() / 2, newX4, newY4, obj.getX(), obj.getY(), obj.getSpriteW(), obj.getSpriteH())) {
                    gameView.destoyWall(obj, true);
                    return collision(getX(), getY() + getSpriteH() / 2, newX4, newY4, obj);
                }
            }

            ArrayList<Objects> objMoveWall = gameView.getObjMoveWall();
            for (Objects obj : objMoveWall) {
                if (up && Geometric_Calculate.interesectionLinesBool(getX() + getSpriteW() / 2, getY(), newX1, newY1, obj.getX(), obj.getY(), obj.getSpriteW(), obj.getSpriteH())) {
                    gameView.destroyMoveWall(obj, true);
                    return collision(getX() + getSpriteW() / 2, getY(), newX1, newY1, obj);
                } else  if (right && Geometric_Calculate.interesectionLinesBool(getX() + getSpriteW(), getY() + getSpriteH() / 2, newX2, newY2, obj.getX(), obj.getY(), obj.getSpriteW(), obj.getSpriteH())) {
                    gameView.destroyMoveWall(obj, true);
                    return collision(getX() + getSpriteW(), getY() + getSpriteH() / 2, newX2, newY2, obj);
                } else  if (down && Geometric_Calculate.interesectionLinesBool(getX() + getSpriteW() / 2, getY() + getSpriteH(), newX3, newY3, obj.getX(), obj.getY(), obj.getSpriteW(), obj.getSpriteH())) {
                    gameView.destroyMoveWall(obj, true);
                    return collision(getX() + getSpriteW() / 2,getY() + getSpriteH(),  newX3, newY3, obj);
                } else  if (left && Geometric_Calculate.interesectionLinesBool(getX(), getY() + getSpriteH() / 2, newX4, newY4, obj.getX(), obj.getY(), obj.getSpriteW(), obj.getSpriteH())) {
                    gameView.destroyMoveWall(obj, true);
                    return collision(getX(), getY() + getSpriteH() / 2, newX4, newY4, obj);
                }
            }

            ArrayList<Objects> objFig = gameView.getObjFigure();
            objFig.addAll(gameView.getObjMoveFigure());
            for (Objects obj : objFig) {

                int whatCollMoreCome = 0;
                boolean isCollision = false;
                float oldDest = 0;
                Objects wallColl = null;

                if (((SimpleFigure) obj).getIsActiveFig()) // если с фигурой можно столкнуться
                    for (Objects wall : ((SimpleFigure) obj).getWalls()) {
                        if (up && Geometric_Calculate.interesectionLinesBool(getX() + getSpriteW() / 2, getY(), newX1, newY1, wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH())) {
                            float thisDest = (float) Geometric_Calculate.destLinePoint(wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH(), getX(), getY());
                            if (!isCollision || oldDest > thisDest) {
                                oldDest = thisDest;
                                whatCollMoreCome = 0;
                                isCollision = true;
                                wallColl = wall;
                            }
                        } else if (right && Geometric_Calculate.interesectionLinesBool(getX() + getSpriteW(), getY() + getSpriteH() / 2, newX2, newY2, wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH())) {
                            float thisDest = (float) Geometric_Calculate.destLinePoint(wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH(), getX(), getY());
                            if (!isCollision || oldDest > thisDest) {
                                oldDest = thisDest;
                                whatCollMoreCome = 1;
                                isCollision = true;
                                wallColl = wall;
                            }
                        } else if (down && Geometric_Calculate.interesectionLinesBool(getX() + getSpriteW() / 2, getY() + getSpriteH(), newX3, newY3, wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH())) {
                            float thisDest = (float) Geometric_Calculate.destLinePoint(wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH(), getX(), getY());
                            if (!isCollision || oldDest > thisDest) {
                                oldDest = thisDest;
                                whatCollMoreCome = 2;
                                isCollision = true;
                                wallColl = wall;
                            }
                        } else if (left && Geometric_Calculate.interesectionLinesBool(getX(), getY() + getSpriteH() / 2, newX4, newY4, wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH())) {
                            float thisDest = (float) Geometric_Calculate.destLinePoint(wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH(), getX(), getY());
                            if (!isCollision || oldDest > thisDest) {
                                oldDest = thisDest;
                                whatCollMoreCome = 3;
                                isCollision = true;
                                wallColl = wall;
                            }
                        }
                    }
                if (isCollision) {
                    return makeCollision(whatCollMoreCome, obj, newX1, newY1, newX2, newY2, newX3, newY3, newX4, newY4, wallColl);
                }
            }

            /*ArrayList<Objects> objMoveFig = gameView.getObjMoveFigure();
            for (Objects obj : objMoveFig) {
                int whatCollMoreCome = 0;
                boolean isCollision = false;
                float oldDest = 0;
                Objects wallColl;
                if (((SimpleFigure) obj).getIsActiveFig())
                    for (Objects wall : ((SimpleFigure) obj).getWalls()) {
                        if (up && Geometric_Calculate.interesectionLinesBool(getX() + getSpriteW() / 2, getY(), newX1, newY1, wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH())) {
                            gameView.destoyMoveFigure(obj, true);
                            return collision(getX() + getSpriteW() / 2, getY(), newX1, newY1, wall);
                        } else if (right && Geometric_Calculate.interesectionLinesBool(getX() + getSpriteW(), getY() + getSpriteH() / 2, newX2, newY2, wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH())) {
                            gameView.destoyMoveFigure(obj, true);
                            return collision(getX() + getSpriteW(), getY() + getSpriteH() / 2, newX2, newY2, wall);
                        } else if (down && Geometric_Calculate.interesectionLinesBool(getX() + getSpriteW() / 2, getY() + getSpriteH(), newX3, newY3, wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH())) {
                            gameView.destoyMoveFigure(obj, true);
                            return collision(getX() + getSpriteW() / 2, getY() + getSpriteH(), newX3, newY3, wall);
                        } else if (left && Geometric_Calculate.interesectionLinesBool(getX(), getY() + getSpriteH() / 2, newX4, newY4, wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH())) {
                            float thisDest = (float) Geometric_Calculate.destLinePoint(wall.getX(), wall.getY(), wall.getSpriteW(), wall.getSpriteH(), getX(), getY() + getSpriteH() / 2);
                            if (!isCollision || oldDest > thisDest) {
                                oldDest = thisDest;
                                whatCollMoreCome = 3;
                                isCollision = true;
                                wallColl = wall;
                            }
                        }
                    }
                if (isCollision) {
                    return makeCollision(whatCollMoreCome, obj, newX1, newY1, newX2, newY2, newX3, newY3, newX4, newY4, wallColl);
                }
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        float newX = getX() + speed * (float) Math.cos(angle),
              newY = getY() + speed * (float) Math.sin(angle),
              newW = newX + getSpriteW() + speed * (float) Math.cos(angle),
              newH = newY + getSpriteH() + speed * (float) Math.sin(angle);

        if (!setX(newX) && (newW <= 0 || newW >= gameView.getWidthMap()))
            return false;
        if (!setY(newY) && (newH <= 0 || newH >= gameView.getHeightMap()))
            return false;
        return true;
    }

    private boolean makeCollision(int whatCollision, Objects obj, float newX1, float newY1, float newX2, float newY2, float newX3, float newY3, float newX4, float newY4, Objects wall) {
        switch (whatCollision) {
            case 0:
                gameView.destoyFigure(obj, true);
                return collision(getX() + getSpriteW() / 2, getY(), newX1, newY1, wall);
            case 1:
                gameView.destoyFigure(obj, true);
                return collision(getX() + getSpriteW(), getY() + getSpriteH() / 2, newX2, newY2, wall);
            case 2:
                gameView.destoyFigure(obj, true);
                return collision(getX() + getSpriteW() / 2, getY() + getSpriteH(), newX3, newY3, wall);
            case 3:
                gameView.destoyFigure(obj, true);
                return collision(getX(), getY() + getSpriteH() / 2, newX4, newY4, wall);
            default:
                return false;
        }
    }

    private boolean collision(float thisX, float thisY, float newX, float newY, Objects obj) {
        if (this instanceof Ball && gameView.getRecord() < paramConst.MAX_SPEED_RECORD)
            ((Ball) this).speed += gameView.getQwadWidth() / 150;
        float XY[] = Geometric_Calculate.intersectionLines(thisX, thisY, newX, newY, obj.getX(), obj.getY(), obj.getSpriteW(), obj.getSpriteH());
        return Move(newAngle(thisX, thisY, newX, newY, XY, obj.getX(), obj.getY(), obj.getSpriteW(), obj.getSpriteH(), ((Ball) this).angle, ((Ball) this).speed - (float) gameView.getQwadWidth() / 150), ((Ball) this).speed);
    }

    /**
     * Расчёт нового угола
     * @param thisX1 исходные координаты точки по X
     * @param thisY1 исходные координаты точки по Y
     * @param thisX2 возможная новая точка по X
     * @param thisY2 возможная новая точка по Y
     * @param XY     координаты пересечения исходной точки по линии движения с линие, координаты которой ниже
     * @param lineX1 начало линии по X
     * @param lineY1 начало линии по Y
     * @param lineX2 конец линии по X
     * @param lineY2 конец линии по Y
     * @param angle исходный угол
     * @return итоговый угол
     */
    private double newAngle(float thisX1, float thisY1, float thisX2, float thisY2, float XY[], float lineX1, float lineY1, float lineX2, float lineY2, double angle, float speed) {
        float thisA = thisY1 - thisY2, thisB = thisX2 - thisX1, thisC = thisX1 * thisY2 - thisX2 * thisY1, // коэфф прямйо движения исходной точки
              lineA = lineY1 - lineY2, lineB = lineX2 - lineX1, lineC = lineX1 * lineY2 - lineX2 * lineY1, // коэфф прямой с которой произошло пересечение
              resLineB = lineA, resLineA = -lineB, resLineC = (-1) * lineA * XY[1] + lineB * XY[1];// перепендикулярная прямая
        double angleLines = Geometric_Calculate.angleLines(thisA, thisB, resLineA, resLineB); // угол между прямыми

        while (angleLines <= 0)
            angleLines += Math.PI * 2;

        if (angleLines > Math.PI / 2) // удастоверяюсь что он меньше 90
            angleLines = Math.PI - angleLines;

        float XY_New[] = Geometric_Calculate.intersectionLines(thisX1, thisY1, thisX2, thisY2, 0, -resLineC / resLineB, 1, -1 * (resLineA + resLineC) / resLineB);

        double invertAngle = angle + Math.PI; //Math.atan2(thisY1 - thisY1 + Math.sin(angle), thisX1 - Math.cos(angle) - thisX1) * (-1); // инвертирую его (вычисляю точку поворота угла по окружности в противоположном направлении)
        float myBeNewX = XY_New[0] + 5 * (float) Math.cos(invertAngle + (angleLines)), // новые возможные координаты
              myBeNewY = XY_New[1] + 5 * (float) Math.sin(invertAngle + (angleLines));
        double angleLines2 = Geometric_Calculate.angleLines(XY_New[1] - myBeNewY, myBeNewX - XY_New[0], resLineA, resLineB); // угол между получившимися прямыми

        while (angleLines2 < 0)
            angleLines2 += Math.PI * 2;

        if (angleLines2 > Math.PI / 2)
            angleLines2 = Math.PI - angleLines2;

        float myBeNewX2 = XY_New[0] + 5 * (float) Math.cos(invertAngle - (angleLines)), // новые возможные координаты
                myBeNewY2 = XY_New[1] + 5 * (float) Math.sin(invertAngle - (angleLines));
        double angleLines3 = Geometric_Calculate.angleLines(XY_New[1] - myBeNewY2, myBeNewX2 - XY_New[0], resLineA, resLineB); // угол между получившимися прямыми


        while (angleLines3 < 0)
            angleLines3 += Math.PI * 2;

        if (angleLines3 > Math.PI / 2)
            angleLines3 = Math.PI - angleLines3;

        // 2 проверки
        // 1: смотрю если угол между новой прямой меньше исходного (мы ближе к перпендикуляру)
        // 2: смотрю пересечение новой возможной точки с прямой (это проверка не всегда срабатывает)
        if (angleLines2 < angleLines3)
            angle = invertAngle + 2 * (angleLines);
        else
            angle = invertAngle - 2 * (angleLines);
        return angle;
    }

    protected void setX_FULL(float x) {
        this.x = x;
    }

    protected void setY_FULL(float y) {
        this.y = y;
    }

    public abstract void draw(Paint paint, Canvas canvas);

    public abstract boolean update(long gameTime);
}
