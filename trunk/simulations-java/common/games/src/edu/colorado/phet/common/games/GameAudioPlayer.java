// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.games;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import edu.colorado.phet.common.phetcommon.audio.AudioResourcePlayer;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Audio player for sound resources.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class GameAudioPlayer extends AudioResourcePlayer {

    protected Clip correctClip;
    private static boolean inited;

    public GameAudioPlayer( boolean enabled ) {
        super( new PhetResources( "games" ), enabled );

        //TODO: Factor out this preloading code if it works acceptably well to reduce audio latency
        URL a = Thread.currentThread().getContextClassLoader().getResource( simResourceLoader.getFullPathForAudio( "correctAnswer.wav" ) );
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream( a );
            correctClip = AudioSystem.getClip();
            correctClip.open( ais );
        }
        catch ( UnsupportedAudioFileException e ) {
            e.printStackTrace();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        catch ( LineUnavailableException e ) {
            e.printStackTrace();
        }
        init();
    }

    //Play a blank audio file to initialize the sound system so that subsequent sounds will play faster
    public void init() {
        if ( !inited ) {
            playSimAudio( "empty.wav" );
            System.out.println( "GameAudioPlayer.init" );
            inited = true;
        }
    }

    public void correctAnswer() {
        System.out.println( "Playing..." );
        correctClip.setFramePosition( 0 );
        correctClip.start();
    }

    public void wrongAnswer() {
        playSimAudio( "wrongAnswer.wav" );
    }

    public void gameOverZeroScore() {
        playSimAudio( "wrongAnswer.wav" );
    }

    public void gameOverImperfectScore() {
        playSimAudio( "gameOver-imperfectScore.wav" );
    }

    public void gameOverPerfectScore() {
        playSimAudio( "gameOver-perfectScore.wav" );
    }
}