package edu.colorado.phet.movingman;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.*;

import edu.colorado.phet.common.phetcommon.audio.PhetAudioClip;

/**
 * @author Sam Reid
 */
public class CrashSound {
    static PreloadedAudioClip preloadedAudioClip = new PreloadedAudioClip( "/moving-man/audio/smash0.wav" );
    private static boolean inited = false;

    /**
     * When using javasound to play an audio clip, the first time each clip is rendered, there is a significant delay, which has led to sim problems
     * such as the audio clip for a crash playing noticeably after the crash occurs.
     * To workaround this problem, the simulation should call init() which plays the sound quietly; playing through the sound once
     * makes subsequent accesses fast.
     */
    public static void init() {
        if ( !inited ) {
            preloadedAudioClip.setGain( -Float.MAX_VALUE );//play this on sim startup so that the next audio play is immediate instead of laggy
            preloadedAudioClip.playAgain();
            System.out.println( "CrashSound.static intializer" );
            inited = true;
        }
    }

    public static void play() {
        preloadedAudioClip.setGain( 0.0f );
        preloadedAudioClip.playAgain();
    }

    public static class PreloadedAudioClip {
        Clip clip;

        public PreloadedAudioClip( String resource ) {
            final InputStream stream = PhetAudioClip.class.getResourceAsStream( resource );
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( stream );
                clip = AudioSystem.getClip();
                clip.open( audioInputStream );
            }
            catch( UnsupportedAudioFileException e ) {
                e.printStackTrace();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            catch( LineUnavailableException e ) {
                e.printStackTrace();
            }
        }

        public void play() {
            clip.start();
        }

        public void playAgain() {
            if ( !clip.isRunning() ) {
                clip.setFramePosition( 0 );
                clip.start();
            }
        }

        public void setGain( float v ) {
            FloatControl gain = (FloatControl) clip.getControl( FloatControl.Type.MASTER_GAIN );//Volume control returns null
            if ( gain != null ) {
                gain.setValue( v );
            }
        }
    }
}