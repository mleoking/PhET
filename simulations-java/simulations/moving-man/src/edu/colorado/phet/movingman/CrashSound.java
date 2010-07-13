package edu.colorado.phet.movingman;

import edu.colorado.phet.common.phetcommon.audio.PhetAudioClip;

/**
 * @author Sam Reid
 */
public class CrashSound {
    
    private static final PhetAudioClip clip = MovingManResources.getInstance().getAudioClip( "smash0.wav" );
    
    public static void play() {
        clip.play();
    }
}