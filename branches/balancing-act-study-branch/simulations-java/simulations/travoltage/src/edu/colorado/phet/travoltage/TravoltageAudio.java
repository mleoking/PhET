// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jul 3, 2006
 * Time: 9:38:53 AM
 */

public class TravoltageAudio implements Runnable {
    private AudioClip[] clips;
    private boolean isPlaying;
    private static final Random random = new Random();
    private int napTime = 1000;
    private boolean sparkFinished = true;

    public TravoltageAudio( TravoltageModel travoltageModel ) {
        travoltageModel.addListener( new TravoltageModel.Listener() {
            public void sparkStarted() {
            }

            public void sparkFinished() {
                sparkFinished = true;
            }

            public void electronExitedFinger() {
                if( sparkFinished ) {
                    play();
                    sparkFinished = false;
                }
            }
        } );
        AudioClip ouch = Applet.newAudioClip( findResource( "travoltage/audio/OuchSmallest.wav" ) );
        AudioClip zzt = Applet.newAudioClip( findResource( "travoltage/audio/ShockSmallest.wav" ) );
        this.clips = new AudioClip[]{ouch, zzt};
    }

    public static URL findResource( String name ) {
        ClassLoader urlLoader = TravoltageAudio.class.getClassLoader();
        return urlLoader.getResource( name );
    }

    public void run() {
        try {
            Thread.sleep( napTime );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
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
