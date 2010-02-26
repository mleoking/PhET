package edu.colorado.phet.reactantsproductsandleftovers;

import edu.colorado.phet.common.phetcommon.audio.AudioResourcePlayer;

/**
 * Audio player for sound resources.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALAudioPlayer extends AudioResourcePlayer {

    public RPALAudioPlayer( boolean enabled ) {
        super( RPALResources.getResourceLoader(), enabled );
    }
    
    public void correctAnswer() {
        playSimAudio( "correctAnswer.wav" );
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
