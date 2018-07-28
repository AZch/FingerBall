package wordcreators.fingerball;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import constants.paramConst;
import maker.Geometric_Calculate;
import maker.Sounds;
import models.Ball;
import models.MoveWall;
import models.Objects;
import models.SimpleFigure;
import models.SimpleWall;

import static android.content.ContentValues.TAG;

/**
 * Created by anton on 23.12.2017.
 */

/**
 *
 *
 *
 *
 *
 *
 * НОРМАЛЬНО ПРОПИСАТЬ РАЗМЕРЫ ОБЪЕКТОВ НА СООТНОШЕНИЕ РАЗМЕРОВ ЭКРАНОВ
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

public class GameView extends SurfaceView implements Runnable {
    public boolean isDraw = true;

    Objects ball;

    private boolean isAddSave = false;

    private int countSave = paramConst.COUNT_SAVE_SQUARE;

    private boolean fMenu = false;

    private int startCountFig = paramConst.startCountFig;
    private int allowCountFig = startCountFig;

    private int startCountMoveFig = paramConst.startCountMoveFig;
    private int allowCountMoveFig = startCountMoveFig;

    private int speedLifeFig = paramConst.START_SPEED_LIFE_FIG;

    private float qwadWidth = 0;
    private float qwadHeight = 0;

    boolean fDoneGame = false;
    boolean fRestart = false;

    private int countBreakWall = 0;

    private boolean stopGame = false; // флаг окончания игры
    public boolean pause = false; // флаг паузы
    boolean drawingMoveWall = false; // флаг указывающий что стену рисуют в данный момент

    private int timeMoveWall = paramConst.COUNT_FIVE_SECOND;
    private int countTimeMoveWall = 0;

    private int countFig = 0;
    private int countMoveFig = 0;

    private Sounds sound;

    // все объекты на карте
    private final ArrayList<ArrayList<models.Objects>> allObj = new ArrayList<>();
    private ArrayList<models.Objects> objPlayer = new ArrayList<>();
    private ArrayList<models.Objects> objWall = new ArrayList<>();
    private ArrayList<models.Objects> objMoveWall = new ArrayList<>();
    private ArrayList<models.Objects> objFigure = new ArrayList<>();
    private ArrayList<models.Objects> objMoveFigure = new ArrayList<>();
    Objects notWall = new SimpleWall(0, 0,0,0, this, false, null, 0, 0, false);

    // размер карты
    private int heightMap = 0, widthMap = 0;

    // рисование карты
    private SurfaceHolder surfaceHolder; // объект, который поддерживает рисование (подходит для рисования при частых обновлениях)
    private Paint paint; // то чем рисуем
    private Canvas canvas; // то на чём рисуем

    // игровой поток
    private Thread gameThread = null;

    public GameWindow gameWindow;

    private Context context;

    public Random random = new Random();

    private boolean isRainbow;

    Point size;

    public GameView(final Context context, int heightMap, int widthMap, boolean fMenu, boolean isRainbow) {
        super(context);

        this.isRainbow = isRainbow;

        sound = new Sounds(context);

        this.fMenu = fMenu;

        this.heightMap = heightMap;
        this.widthMap = widthMap;

        this.context = context;

        //Objects ball = new Ball(300, 300, 30, 30, this, context);
        //objPlayer.add(ball);

        allObj.add(objWall);
        allObj.add(objMoveWall);
        allObj.add(objFigure);
        allObj.add(objMoveFigure);
        allObj.add(objPlayer);

        if (fMenu)
            setMenuParam();
        else
            setGameParam();

        surfaceHolder = getHolder();
        paint = new Paint();
    }

    public void makeHit() {
        sound.Hit();
    }

    public void stopThread() {
        stopGame = true;
    }

    private void setMenuParam() {
        timeMoveWall = paramConst.MAX_FPS / 5;

        speedLifeFig = paramConst.maxSpeedLifeFig / 2;

        startCountFig = paramConst.MAX_FIGURE;
        allowCountFig = startCountFig;

        startCountMoveFig = paramConst.MAX_MOVE_FIGURE;
        allowCountMoveFig = startCountMoveFig;
    }

    private void setGameParam() {
        timeMoveWall = paramConst.COUNT_FIVE_SECOND;

        speedLifeFig = paramConst.START_SPEED_LIFE_FIG;

        startCountFig = paramConst.startCountFig;
        allowCountFig = startCountFig;

        startCountMoveFig = paramConst.startCountMoveFig;
        allowCountMoveFig = startCountMoveFig;
    }

    public float getQwadWidth() {
        return qwadWidth;
    }

    public float getQwadHeight() {
        return qwadHeight;
    }

    @Override
    public void run() {
        while(true) {
            countSave = paramConst.COUNT_SAVE_SQUARE;
            if (stopGame)
                return;
            if (fDoneGame)
                continue;
            if (surfaceHolder.getSurface().isValid()) {
                this.heightMap = surfaceHolder.getSurfaceFrame().height();
                this.widthMap = surfaceHolder.getSurfaceFrame().width();

                qwadHeight = heightMap / 64;
                qwadWidth = heightMap / 64;
                if (!fMenu) {
                    ball = new Ball(widthMap / 2 - qwadWidth / 2, heightMap / 2 - qwadHeight / 2, qwadWidth, qwadHeight, this, context, qwadWidth / 6);
                    objPlayer.add(ball);
                }
                boolean dontPlaying = false;

                long startTime; // время начала цикла
                long stepTime; // время выполнения шага цикла
                int sleepTime = 0; // сколько можно спать в мс (если < 0, то выполнение опаздывает)
                int framesSkipped = 0; // кол-во кадров у которых не выводилась графика на экран
                if (isDraw)
                    draw();

                while (!dontPlaying) {
                    //Log.d(TAG, "run: count obj: " + getCountAllObj());


                    if (fRestart) {
                        if (isDraw)
                            draw();
                        break;
                    }

                    if (fDoneGame) {
                        if (isDraw)
                            draw();
                        break;
                    }

                    if (stopGame) { // если игра закончена
                        return;
                    }

                    if (pause) {
                        if (isDraw)
                            draw();
                        continue;
                    }

                    try {
                        startTime = System.currentTimeMillis();
                        framesSkipped = 0;

                        dontPlaying = update();

                        draw();

                        stepTime = System.currentTimeMillis() - startTime;
                        sleepTime = (int) (paramConst.FRAME_PERIOD - stepTime);

                        if (dontPlaying)
                            break;

                        if (sleepTime > 0)
                            pause(sleepTime);

                        while (sleepTime < 0 && framesSkipped < paramConst.MAX_FRAME_SKIPS) {
                            dontPlaying = update();
                            if (dontPlaying) {
                                draw();
                                break;
                            }
                            sleepTime += paramConst.FRAME_PERIOD;
                            framesSkipped++;
                        }
                    } finally {

                    }
                }

                if (fRestart) {
                    fRestart = false;
                    clearObjects();
                    countBreakWall = 0;
                    countFig = 0;
                    countMoveFig = 0;
                    continue;
                }

                if (!fMenu) {
                    clearObjects();
                    doneGame(countBreakWall);
                    countBreakWall = 0;
                    countFig = 0;
                    countMoveFig = 0;
                }
            }

        }
    }

    public int getCountAllObj() {
        int count = 0;
        for (ArrayList<Objects> objs : allObj) {
            count += objs.size();
        }
        return count;
    }

    private void doneGame(int countWall) {
        if (gameWindow.maxBreakWall < countWall) {
            gameWindow.newRecord(countWall);
        }
        fDoneGame = true;
        gameWindow.doneGame(countWall);

    }

    public boolean update() {
        boolean flagResult = false;
        if (!fMenu) {
            if (countBreakWall > 5)
                startPrintFigure();
            if (countBreakWall > 2)
                startMoveLine();
            if (countBreakWall > 10)
                startPrintMoveFigure();
            if (!isAddSave && countBreakWall != 0 && ((float) countBreakWall) % ((float) 20) == 0) {
                countSave++;
                isAddSave = true;
                updateSaveText();
            } if (countBreakWall != 0 && ((float) countBreakWall) % ((float) 20) != 0)
                isAddSave = false;

        } else {
            startPrintFigure();
            startMoveLine();
            startPrintMoveFigure();
        }
        for (Objects obj : objPlayer)
            if (obj.update(System.currentTimeMillis()))
                flagResult = true;
        try {
            for (Objects obj : objFigure) {
                if (obj.update(System.currentTimeMillis())) {
                    if (((SimpleFigure) obj).getSpeed() == 0)
                        destoyFigure(obj, false);
                    else
                        destoyMoveFigure(obj, false);
                }
            }
        }
        catch (Exception e) {

        }

        try {
            for (Objects obj : objMoveWall) {
                if (obj.update(System.currentTimeMillis()))
                    objMoveWall.remove(obj);
            }

            for (Objects obj : objWall) {
                if (obj.update(System.currentTimeMillis()))
                    objWall.remove(obj);
            }
        }
        catch (Exception e) {

        }

        return flagResult;

        //return false;
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            try {
                canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.LTGRAY);

                for (ArrayList<Objects> objs : allObj)
                    for (Objects obj : objs)
                        if (obj instanceof MoveWall)
                            obj.draw(paint, canvas);
                        else
                            obj.draw(paint, canvas);
                if (drawingMoveWall)
                    notWall.draw(paint, canvas);


                //gameWindow.score.setText(String.valueOf(countBreakWall));
                if (!fMenu) {
                    paint.setColor(Color.GRAY);
                    paint.setTextSize(qwadWidth + qwadWidth / 3);
                    canvas.drawText(String.valueOf(countBreakWall) + " / " + String.valueOf(gameWindow.maxBreakWall), surfaceHolder.getSurfaceFrame().width() / 2, surfaceHolder.getSurfaceFrame().height() / 20, paint);
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                e.printStackTrace();
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void updateSaveText() {
        gameWindow.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameWindow.updateCountSave(countSave);
            }
        });
    }

    public boolean startPause() {
        if (pause)
            pause = false;
        else
            pause = true;

        return pause;
    }

    public void startGame() {
        clearObjects();
        countSave = paramConst.COUNT_SAVE_SQUARE;
        fDoneGame = false;
        pause = false;
    }

    public void drawLine(float startX, float startY, float endX, float endY) {
        try {
            if (surfaceHolder.getSurface().isValid()) {
                SimpleWall simpleWall;

                float speed = 0;
                try {
                    if (!fMenu) {
                        if (objPlayer.size() != 0)
                            speed = 3 * ((Ball) objPlayer.get(0)).getSpeed() / 4;
                    }
                    else
                        speed = paramConst.START_SPEED_LIFE_FIG * 2;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (countBreakWall > -1) // -1
                    simpleWall = new SimpleWall(startX, startY, endY, endX, this, false, null,
                            speed, (-1) * Math.atan2(startY - endY, endX - startX), true);
                else
                    simpleWall = new SimpleWall(startX, startY, endY, endX, this, false, null, 0, 0, true);
                if (objWall.size() == paramConst.maxDrawWall)
                    objWall.remove(0);
                objWall.add(simpleWall);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // пауза
    private void pause(int sleepTime) {
        try {
            gameThread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clearObjects() {
        for (ArrayList<Objects> listObj : allObj)
            listObj.clear();
    }

    public void destoyWall(Objects wall,boolean add) {
        objWall.remove(wall);
        if (add) {
            makeHit();
            countBreakWall++;
        }
    }

    public void destoyFigure(Objects figure, boolean add) {
        objFigure.remove(figure);
        countFig--;
        if (add) {
            makeHit();
            countBreakWall++;
        }
    }

    public void destoyMoveFigure(Objects figure, boolean add) {
        objFigure.remove(figure);
        countMoveFig--;
        if (add) {
            makeHit();
            countBreakWall++;
        }
    }

    public void destroyMoveWall(Objects figure, boolean add) {
        objMoveWall.remove(figure);
        if (add) {
            makeHit();
            countBreakWall++;
        }
    }

    private void createSimpleFigure() {
        if (isFigure()) {
            countFig++;
            if (!fMenu) {
                if (speedLifeFig < paramConst.maxSpeedLifeFig) {
                    if (countBreakWall / 30 > 0)
                        speedLifeFig = paramConst.START_SPEED_LIFE_FIG + (int) countBreakWall / 30;
                } else
                    speedLifeFig = paramConst.maxSpeedLifeFig;
            }

            float XY[] = Geometric_Calculate.findPlaceFig(1, this);
            SimpleFigure simpleFigure = new SimpleFigure(XY[0], XY[1], 0, 0, this, random.nextInt(5), random.nextInt((int)(qwadWidth + qwadHeight)) + qwadWidth + qwadHeight, 0, 0, speedLifeFig);
            objFigure.add(simpleFigure);
        }
    }

    private boolean isFigure() {
        if (!fMenu) {
            if (countFig > paramConst.MAX_FIGURE)
                allowCountFig = paramConst.MAX_FIGURE;
            else
                allowCountFig = paramConst.startCountFig + (int) countBreakWall / 10;
        }
        if (countFig > allowCountFig)
            return false;
        return true;
    }

    private void startPrintFigure() {
        createSimpleFigure();
    }

    private void startMoveLine() {
        if (!fMenu) {
            if (paramConst.MAX_FPS / 5 < paramConst.COUNT_FIVE_SECOND - countBreakWall * 3)
                timeMoveWall = paramConst.COUNT_FIVE_SECOND - countBreakWall * 3;
            else
                timeMoveWall = paramConst.MAX_FPS / 5;
        }
        if (countTimeMoveWall <= 0) {
            Random random = new Random();
            MoveWall moveWall;
            float point = 0;
            float speed = 0;

            if (!fMenu) {
                speed = ((Ball) objPlayer.get(0)).getSpeed();
            } else
                speed = paramConst.START_SPEED_LIFE_FIG * 2;

            switch (random.nextInt(4)) {
                case 0:
                    point = random.nextInt(getWidthMap());
                    moveWall = new MoveWall(point, 1, 0, 0, this, (-1) * Math.atan2(1 - getHeightMap(), random.nextInt(getWidthMap()) - point),
                            random.nextInt((int) speed) + (int) speed, random.nextInt(100) + 60);
                    objMoveWall.add(moveWall);
                    break;
                case 1:
                    point = random.nextInt(getHeightMap());
                    moveWall = new MoveWall(1, point, 0, 0, this, (-1) * Math.atan2(point - random.nextInt(getHeightMap()), getWidthMap() - 1),
                            random.nextInt((int) speed) + (int) speed, random.nextInt(100) + 60);
                    objMoveWall.add(moveWall);
                    break;
                case 2:
                    point = random.nextInt(getWidthMap());
                    moveWall = new MoveWall(point, getHeightMap() - 1, 0, 0, this, (-1) * Math.atan2(getHeightMap() - 1 - random.nextInt(getHeightMap()), 0 - point),
                            random.nextInt((int) speed) + (int) speed, random.nextInt(100) + 60);
                    objMoveWall.add(moveWall);
                    break;
                default:
                    point = random.nextInt(getHeightMap());
                    moveWall = new MoveWall(getWidthMap() - 1, point, 0, 0, this, (-1) * Math.atan2(point - getHeightMap(), random.nextInt(getWidthMap()) - getWidthMap() - 1),
                            random.nextInt((int) speed) + (int) speed, random.nextInt(100) + 60);
                    objMoveWall.add(moveWall);
                    break;
            }

            if (moveWall.update(System.currentTimeMillis()))
                objMoveWall.remove(moveWall);
            countTimeMoveWall = timeMoveWall;
        } else {
            countTimeMoveWall--;
        }
    }

    private boolean isMoveFigure() {
        if (!fMenu) {
            if (countMoveFig > paramConst.MAX_MOVE_FIGURE)
                allowCountMoveFig = paramConst.MAX_MOVE_FIGURE;
            else
                allowCountMoveFig = paramConst.startCountMoveFig + (int) countBreakWall / 10;
        }
        if (countMoveFig > allowCountMoveFig)
            return false;
        return true;
    }

    private void startPrintMoveFigure() {
        if (isMoveFigure()) {
            countMoveFig++;
            if (!fMenu) {
                if (speedLifeFig < paramConst.maxSpeedLifeFig) {
                    if (countBreakWall / 30 > 0)
                        speedLifeFig = paramConst.START_SPEED_LIFE_FIG + (int) countBreakWall / 30;
                } else
                    speedLifeFig = paramConst.maxSpeedLifeFig;
            }
            float XY[] = Geometric_Calculate.findPlaceFig(50, this);
            float speed = 0;
            if (!fMenu) {
                speed = ((Ball) objPlayer.get(0)).getSpeed();
            } else
                speed = paramConst.START_SPEED_LIFE_FIG;
            SimpleFigure simpleFigure = new SimpleFigure(XY[0], XY[1], 0, 0, this, random.nextInt(5), random.nextInt((int)(qwadWidth + qwadHeight)) + qwadWidth + qwadHeight,
                    random.nextInt((int)speed) + speed / 2, random.nextInt(360) * Math.PI / 180, speedLifeFig);
            objFigure.add(simpleFigure);
        }
    }

    public void addObjectFigure(SimpleFigure simpleFigure) {
        objFigure.add(simpleFigure);
    }

    public void addSaveSquare() {
        if (countSave > 0) {
            countSave--;
            objFigure.add(new SimpleFigure(4, 4, size.y - 4, size.x - 4, this, paramConst.RECTANGLE_FIG, 0, 0, 0, speedLifeFig));
        }
    }

    public ArrayList<Objects> getObjWall() {
        return objWall;
    }

    public ArrayList<Objects> getObjFigure() {
        return objFigure;
    }

    public ArrayList<Objects> getObjMoveWall() {
        return objMoveWall;
    }

    public ArrayList<Objects> getObjMoveFigure() {
        return objMoveFigure;
    }

    // получить максимальное кол-во квадратов по вертикали карты
    public int getHeightMap() { return heightMap; }

    // получить максимальное кол-во квадратов по горизонтали карты
    public int getWidthMap() { return widthMap; }

    public Objects getBall() {
        return ball;
    }

    public int getRecord() {
        return countBreakWall;
    }

    public boolean IsRainbow() {
        return isRainbow;
    }

    public void setRainbow(boolean isRainbow) {
        this.isRainbow = isRainbow;
    }

    public int getCountSave() {
        return countSave;
    }

    public boolean getStopGame() {
        return stopGame;
    }

    public boolean getPauseGame() {
        return pause;
    }

    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;

        Display display = gameWindow.getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }
}
