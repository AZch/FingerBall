package maker;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import wordcreators.fingerball.MainActivity;
import wordcreators.fingerball.R;

/**
 * Created by anton on 08.01.2018.
 */

public class Sounds {
    // звук выстрела
    private SoundPool soundPool = null;
    // id звукового файла удар
    private int idSoundHit = 0;


    // контекст
    Context context;

    public Sounds(Context context) {
        this.context = context;
        soundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 0);

        idSoundHit = soundPool.load(context, R.raw.hit, 1);
    }

    // воспроизвести звук удара
    public void Hit() {
        try {
            soundPool.play(idSoundHit, (float)1, (float)1, 1, 0, 1);
        } catch (Exception e) {
            killSounds();
            new Sounds(context);
            soundPool.play(idSoundHit, (float) 1, (float) 1, 1, 0, 1);
        }
    }

    public void killSounds() {
        soundPool.release();
    }
}
