package edu.utep.cs.cs4381.rocketshooter.model;


import android.content.Context;
import android.media.SoundPool;

import edu.utep.cs.cs4381.rocketshooter.R;

/**
 * Class in charge of playing sound clips
 */
public class SoundPlayer {
    private final SoundPool soundPool;

    private static SoundPlayer instance;

    public enum Sound {
        ENEMY_EXPLOSION(R.raw.enemy_explosion),
        ENEMY_SHOOT(R.raw.enemy_shoot),
        PLAYER_SHOOT(R.raw.player_shoot),
        PLAYER_EXPLOSION(R.raw.player_explosion),
        PLAYER_WIN(R.raw.player_win),
        EXTRA_LIFE(R.raw.extra_life),
        BARRIER_HIT(R.raw.barrier_hit);

        public final int resourceId;
        private int soundId;

        Sound(int resourceId) {
            this.resourceId = resourceId;
        }
    }

    private SoundPlayer(Context context) {
        soundPool = new SoundPool.Builder().setMaxStreams(Sound.values().length).build();
        for (Sound sound : Sound.values()) {
            sound.soundId = soundPool.load(context, sound.resourceId, 1);
        }
    }

    public static SoundPlayer instance(Context context) {
        if (instance == null) {
            instance = new SoundPlayer(context);
        }
        return instance;
    }

    public void play(Sound sound) {
        soundPool.play(sound.soundId, 1, 1, 0, 0, 1);
    }
}
