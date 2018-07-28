package wordcreators.fingerball;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import constants.paramConst;
import constants.requestCodes;
import constants.requestString;

public class MainActivity extends AppCompatActivity  {

    SharedPreferences saveLoadRecord;

    private GameView gameView;

    private Thread menuThread;

    private int recordInt = 0;

    private TextView record;
    private ImageView startGame;
    private ImageView startGameDown;

    private ImageView rainbowOn;
    private ImageView rainbowOff;

    public boolean isRainbow = false;

    private LinearLayout menuLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //gameView = new GameView(this, 500, 500, true);

        menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
        //menuLayout.addView(gameView);

        record = (TextView) findViewById(R.id.scoreMenu);
        startGame = (ImageView) findViewById(R.id.startButton);
        startGameDown = (ImageView) findViewById(R.id.startButtonDown);

        rainbowOff = findViewById(R.id.rainbowoff);
        rainbowOn = findViewById(R.id.rainbowon);

        startGameDown.setVisibility(View.GONE);

        loadRecord();

        //menuThread = new Thread(gameView);
        //menuThread.start();
    }

    public void startButtonTouch(View view) {
        try {
            startGameDown.setVisibility(View.VISIBLE);
            startGame.setVisibility(View.INVISIBLE);

            Intent intent = new Intent(this, GameWindow.class);

            startActivityForResult(intent, requestCodes.REQUEST_PARAM);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rainbowClickOn(View view) {
        rainbowOn.setVisibility(View.VISIBLE);
        rainbowOff.setVisibility(View.INVISIBLE);
        isRainbow = false;
        gameView.setRainbow(isRainbow);
        saveRainbow();
    }

    public void rainbowClickOff(View view) {
        rainbowOn.setVisibility(View.VISIBLE);
        rainbowOff.setVisibility(View.VISIBLE);
        isRainbow = true;
        gameView.setRainbow(isRainbow);
        saveRainbow();
    }

    private void saveRainbow() {
        SharedPreferences.Editor prefEditor = saveLoadRecord.edit();

        prefEditor.putBoolean(paramConst.RAINBOW, isRainbow);

        prefEditor.apply();
    }


    @Override
    protected void onPause() {
        super.onPause();

        gameView.stopThread();
        gameView = null;

        menuLayout.removeAllViews();

        menuThread = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        gameView = new GameView(this, 500, 500, true, isRainbow);
        gameView.setRainbow(isRainbow);

        menuLayout.addView(gameView);

        menuThread = new Thread(gameView);
        menuThread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int thisRecord = 0;

        if (resultCode == RESULT_OK) {
            if (requestCode == requestCodes.REQUEST_PARAM) {
                thisRecord = data.getIntExtra(requestString.RECORD, 0);
                if (thisRecord > recordInt) {
                    recordInt = thisRecord;
                    saveRecord();
                    record.setText(String.valueOf(recordInt));
                }
            }

        }

        // gameView.startPause();

        startGame.setVisibility(View.VISIBLE);
        startGameDown.setVisibility(View.GONE);
    }

    public void loadRecord() {
        saveLoadRecord = getSharedPreferences(paramConst.PREFS_FILE, MODE_PRIVATE);

        recordInt = saveLoadRecord.getInt(paramConst.RECORD, 0);

        isRainbow = saveLoadRecord.getBoolean(paramConst.RAINBOW, false);
        if (isRainbow) {
            rainbowOff.setVisibility(View.INVISIBLE);
            rainbowOn.setVisibility(View.VISIBLE);
        } else {
            rainbowOff.setVisibility(View.VISIBLE);
            rainbowOn.setVisibility(View.INVISIBLE);
        }

        record.setText(String.valueOf(recordInt));
    }

    public void saveRecord() {
        SharedPreferences.Editor prefEditor = saveLoadRecord.edit();

        prefEditor.putInt(paramConst.RECORD, recordInt);

        prefEditor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //saveParams();
    }
}
