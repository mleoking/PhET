package edu.colorado.phet.travoltage;

import java.applet.AudioClip;
import java.util.Random;

public class AudioProxy implements Runnable {
    AudioClip[] clips;
    boolean isPlaying;
    static final Random random = new Random();
    int napTime;

    public AudioProxy( AudioClip[] clips, int napTime ) {
        this.clips = clips;
        this.napTime = napTime;
    }

    public void run() {
        edu.colorado.phet.common.util.ThreadHelper.quietNap( napTime );
        isPlaying = false;
    }

    public void play() {
        if( isPlaying ) {
            return;
        }
        else {
            isPlaying = true;
            int rand = random.nextInt( clips.length );
            clips[rand].play();
            new Thread( this ).start();
        }
    }
}
