// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro.view;

import edu.colorado.phet.common.phetcommon.audio.AudioResourcePlayer;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Audio player for sound resources.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class GameAudioPlayer extends AudioResourcePlayer {

    public static final GameAudioPlayer instance = new GameAudioPlayer( true );

    private GameAudioPlayer( boolean enabled ) {
        super( new PhetResources( "games" ), enabled );
    }

    public void correctAnswer() {
        playSimAudio( "correctAnswer.wav" );
    }

    public void gameOverPerfectScore() {
        playSimAudio( "gameOver-perfectScore.wav" );
    }
}