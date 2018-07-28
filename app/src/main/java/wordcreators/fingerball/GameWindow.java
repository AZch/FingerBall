package wordcreators.fingerball;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.nfc.Tag;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import constants.paramConst;
import constants.requestCodes;
import constants.requestString;
import models.SimpleWall;

import static android.content.ContentValues.TAG;

public class GameWindow extends AppCompatActivity implements View.OnTouchListener {

    SharedPreferences saveRecord;

    int maxBreakWall;

    int thisBreakWall;

    boolean isDrawing = false;

    float startX, startY;

    private GameView gameView;

    ConstraintLayout allGameElement;

    public Thread gameThread;

    ImageView next;

    ImageView stop;

    ImageView restart;

    ImageView restartPause;

    TextView score;

    TextView countSaveText;

    ImageView saveButton;

    // игровая поверхность
    LinearLayout gameLayout;

    boolean isRainbow;

    Animation animRectangle;
    boolean endAnimRectangle = true;

    //private InterstitialAd mInterstitialAd;

    InterstitialAd mInterstitialAd;
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_game_window);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        saveRecord = getSharedPreferences(paramConst.PREFS_FILE, MODE_PRIVATE);

        maxBreakWall = saveRecord.getInt(paramConst.RECORD, 0);

        isRainbow = saveRecord.getBoolean(paramConst.RAINBOW, false);

        gameView = new GameView(this, 500, 500, false, isRainbow);

        gameView.gameWindow = this;
        gameView.setGameWindow(this);

        gameLayout = (LinearLayout) findViewById(R.id.gameLayout);
        gameLayout.addView(gameView);
        gameLayout.setOnTouchListener(this);

        allGameElement = (ConstraintLayout) findViewById(R.id.allGameElementLayout);
        allGameElement.setOnTouchListener(this);

        next = (ImageView) findViewById(R.id.next);
        stop = (ImageView) findViewById(R.id.stop);
        restart = (ImageView) findViewById(R.id.restart);
        score = (TextView) findViewById(R.id.doneScore);
        restartPause = (ImageView) findViewById(R.id.restartPause);
        saveButton = findViewById(R.id.save);
        countSaveText = findViewById(R.id.countSave);


        MobileAds.initialize(this, "ca-app-pub-2391831280029080~1351925567");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2391831280029080/2082553302");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });



        // запуск самой игры
        gameThread = new Thread(gameView);
        gameThread.start();
        countSaveText.setText(String.valueOf(gameView.getCountSave()));
    }

    public void newRecord(int record) {
        maxBreakWall = record;

        SharedPreferences.Editor prefEditor = saveRecord.edit();

        prefEditor.putInt(paramConst.RECORD, record);

        prefEditor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (gameView != null) {
            if (gameView.getPauseGame()) {
                next.setVisibility(View.VISIBLE);
                stop.setVisibility(View.VISIBLE);
                restartPause.setVisibility(View.VISIBLE);
            }
            gameView.isDraw = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (gameView != null) {
            gameView.pause = true;
            if (gameView.getPauseGame()) {
                next.setVisibility(View.VISIBLE);
                stop.setVisibility(View.VISIBLE);
                restartPause.setVisibility(View.VISIBLE);
            }
            gameView.isDraw = false;
        }

    }

    public void doneGame(int record) {
        thisBreakWall = record;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
                next.setVisibility(View.INVISIBLE);
                restartPause.setVisibility(View.INVISIBLE);
                restart.setVisibility(View.VISIBLE);
                stop.setVisibility(View.VISIBLE);

                if (thisBreakWall > maxBreakWall) {
                    score.setText(String.valueOf(thisBreakWall));
                } else
                    score.setText(String.valueOf(thisBreakWall) + " / " + String.valueOf(maxBreakWall));

                score.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.gameLayout:

                if (!gameView.getStopGame() && !gameView.getPauseGame())
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            isDrawing = true;
                            startX = motionEvent.getX();
                            startY = motionEvent.getY();
                            //invalidate();
                            if (gameView.notWall instanceof SimpleWall) {
                                ((SimpleWall) gameView.notWall).setStartCoord(motionEvent.getX(), motionEvent.getY());
                                ((SimpleWall) gameView.notWall).setEndCoord(motionEvent.getX(), motionEvent.getY());
                            }
                            gameView.drawingMoveWall = true;
                            view.invalidate();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            //invalidate();
                            if (gameView.notWall instanceof SimpleWall && gameView.drawingMoveWall)
                                ((SimpleWall) gameView.notWall).setEndCoord(motionEvent.getX(), motionEvent.getY());
                            view.invalidate();
                            break;
                        case MotionEvent.ACTION_UP:
                            isDrawing = false;
                            gameView.drawingMoveWall = false;
                            gameView.drawLine(startX, startY, motionEvent.getX(), motionEvent.getY());
                            view.invalidate();
                            break;
                    }

                break;
        }
        return true;
    }

    public void updateCountSave(int countSave) {
        countSaveText.setText(String.valueOf(countSave));
    }

    @Override
    public void onBackPressed() {
        if (!gameView.fDoneGame) {
            if (gameView.startPause()) {
                next.setVisibility(View.VISIBLE);
                stop.setVisibility(View.VISIBLE);
                restartPause.setVisibility(View.VISIBLE);
            } else {
                score.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                restartPause.setVisibility(View.GONE);
            }
        }
    }

    public void pause(View view) {
        if (!gameView.fDoneGame) {
            if (gameView.startPause()) {
                next.setVisibility(View.VISIBLE);
                stop.setVisibility(View.VISIBLE);
                restartPause.setVisibility(View.VISIBLE);
            } else {
                score.setVisibility(View.GONE);
                next.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                restartPause.setVisibility(View.GONE);
            }
        }
    }

    public void next(View view) {
        gameView.startPause();
        score.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        stop.setVisibility(View.GONE);
        restartPause.setVisibility(View.GONE);
    }

    public void stop(View view) {
        gameView.stopThread();

        Intent data = new Intent();
        data.putExtra(requestString.RECORD, maxBreakWall);
        setResult(RESULT_OK, data);

        gameView = null;
        finish();
    }

    public void save(View view) {
        if (endAnimRectangle) {
            animRectangle = AnimationUtils.loadAnimation(this, R.anim.movesquare);
            animRectangle.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    endAnimRectangle = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    endAnimRectangle = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    endAnimRectangle = false;
                }
            });
            saveButton.startAnimation(animRectangle);
            gameView.addSaveSquare();
            countSaveText.setText(String.valueOf(gameView.getCountSave()));
        }
    }

    public void restart(View view) {
        next.setVisibility(View.GONE);
        restart.setVisibility(View.GONE);
        stop.setVisibility(View.GONE);
        score.setVisibility(View.GONE);
        restartPause.setVisibility(View.GONE);

        countSaveText.setText(String.valueOf(paramConst.COUNT_SAVE_SQUARE));

        gameView.startGame();
        countSaveText.setText(String.valueOf(gameView.getCountSave()));
    }

    public void restartPause(View view) {
        gameView.fRestart = true;
        next.setVisibility(View.GONE);
        restart.setVisibility(View.GONE);
        stop.setVisibility(View.GONE);
        score.setVisibility(View.GONE);
        restartPause.setVisibility(View.GONE);

        gameView.startGame();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            //if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
            //gameView.setPortrait();
            //else
            //gameView.setLandscape();
        }
        catch (Exception e) {
            Log.d(TAG, "onConfigurationChanged: " + e);
        }
    }
}
