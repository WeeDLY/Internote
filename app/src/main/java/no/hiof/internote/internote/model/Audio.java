package no.hiof.internote.internote.model;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

public class Audio {
    public static void playSound(Context ctx, String soundFile) {
        if(!Settings.getSound()){
            return;
        }
        MediaPlayer m = new MediaPlayer();
        try {
            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = ctx.getAssets().openFd(soundFile);
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.start();
            Log.d("PLAY_SOUND", soundFile + " YES");
        } catch (Exception e) {
            Log.d("PLAY_SOUND", soundFile + " NO");
            e.printStackTrace();
        }
    }
}
