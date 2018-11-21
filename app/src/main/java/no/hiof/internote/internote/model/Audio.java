package no.hiof.internote.internote.model;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

/*
    Audio class: Plays sounds
 */
public class Audio {
    /*
        Method: Plays a sounds
     */
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
