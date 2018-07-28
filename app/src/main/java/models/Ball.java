package models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

import constants.paramConst;
import wordcreators.fingerball.GameView;
import wordcreators.fingerball.R;

/**
 * Created by anton on 24.12.2017.
 */

public class Ball extends Objects {
    protected double angle = Math.PI / 2;
    protected float speed = paramConst.SPEED_BALL;

    public Ball(float x, float y, float spriteH, float spriteW, GameView gameView, Context context, float speed) {
        super(x, y, spriteH, spriteW, gameView);

        this.speed = speed;
        Random random = new Random();
        angle = random.nextInt((int) Math.PI) + random.nextFloat();
        bitMapId = R.drawable.player;
        init(context);
    }

    @Override
    public void draw(Paint paint, Canvas canvas) {
        Rect srcRect = new Rect(0, 0, (int)getSpriteW(), (int)getSpriteH()),
              destRect = new Rect((int)getX(), (int)getY(), (int)getX() + (int)getSpriteW(), (int)getY() + (int)getSpriteH());
        canvas.drawBitmap(bitmap, srcRect, destRect, paint);
    }

    public float getSpeed() {
        return speed;
    }

    @Override
    public boolean update(long gameTime) {
        if (Move(angle, speed)) {
            // движение просто прямо

            return false;
        }
        else {

            return true;
        }
        //return false;
    }
}
