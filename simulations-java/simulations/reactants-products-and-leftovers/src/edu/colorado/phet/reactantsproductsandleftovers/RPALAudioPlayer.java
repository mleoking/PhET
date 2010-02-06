package edu.colorado.phet.reactantsproductsandleftovers;

import edu.colorado.phet.reactantsproductsandleftovers.util.AudioResourcePlayer;


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
    
    public void gameOverImperfectScore() {
        playSimAudio( "gameOver-imperfectScore.wav" );
    }
    
    public void gameOverPerfectScore( int level ) {
        switch ( level ) {
        case 1:
            playSimAudio( "gameOver-perfectScore-level1.wav" );
            break;
        case 2:
            playSimAudio( "gameOver-perfectScore-level2.wav" );
            break;
        case 3:
            playSimAudio( "gameOver-perfectScore-level3.wav" );
            break;
        default:
            System.err.println( "RPALAudioPlayer.gameOverPerfect: unsupported level=" + level );
        }
    }
}
