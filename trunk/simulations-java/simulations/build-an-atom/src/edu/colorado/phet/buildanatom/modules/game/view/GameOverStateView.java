// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;

import java.text.DecimalFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.games.GameOverNode;

/**
 * This displays a piccolo dialog on the screen after a game is complete, indicating the score, elapsed time, whether
 * the time is the best time, etc.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class GameOverStateView extends StateView {
    private final GameOverNode gameOverNode;
    private final GameAudioPlayer gameAudioPlayer;

    /**
     * Constructor.
     */
    public GameOverStateView( BuildAnAtomGameCanvas gameCanvas, final BuildAnAtomGameModel model ) {
        super( model, model.getGameOverState(), gameCanvas );
        gameAudioPlayer = new GameAudioPlayer( model.isSoundEnabled() ); // Assumes that the game over state is recreated at the end of each game.
        gameAudioPlayer.init();
        gameOverNode = new GameOverNode( model.getLevel(), model.getScore(),
                                         model.getMaximumPossibleScore(), new DecimalFormat( "0.#" ), model.getTime(), model.getBestTime( model.getLevel() ),
                                         model.isNewBestTime(), model.isTimerEnabled() );
        gameOverNode.setScale( 1.5 ); // Scale arbitrarily chosen to look good.
        gameOverNode.addGameOverListener( new GameOverNode.GameOverListener() {
            public void newGamePressed() {
                model.newGame();
            }
        } );
    }

    /**
     * Adds the node that conveys that the game is over and also plays the
     * appropriate audio based on how the user did and whether the sound is
     * enabled.
     */
    @Override
    public void init() {
        gameOverNode.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width / 2 - gameOverNode.getFullBoundsReference().width / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - gameOverNode.getFullBoundsReference().height / 2 );
        addChild( gameOverNode );
        if ( getModel().getScore() == 0 ) {
            gameAudioPlayer.gameOverZeroScore();
        }
        else if ( getModel().getScore() == getModel().getMaximumPossibleScore() ) {
            gameAudioPlayer.gameOverPerfectScore();
        }
        else {
            gameAudioPlayer.gameOverImperfectScore();
        }
    }

    @Override
    public void teardown() {
        removeChild( gameOverNode );
    }
}
