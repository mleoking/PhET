// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;

import java.text.DecimalFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.games.GameOverNode;

//DOC
/**
* @author Sam Reid
*/
public class GameOverStateView extends StateView {
    private final GameOverNode gameOverNode;
    private final GameAudioPlayer gameAudioPlayer;

    public GameOverStateView( BuildAnAtomGameCanvas gameCanvas, final BuildAnAtomGameModel model ) {
        super( model, model.getGameOverState(), gameCanvas );
        gameAudioPlayer = new GameAudioPlayer( model.getSoundEnabledProperty().getValue() );
        gameOverNode = new GameOverNode( model.getLevelProperty().getValue(), model.getScore(),
                model.getMaximumPossibleScore(), new DecimalFormat( "0.#" ), model.getTime(), model.getBestTime( model.getCurrentLevel() ),
                model.isNewBestTime(), model.getTimerEnabledProperty().getValue() );
        gameOverNode.setScale( 1.5 );
        gameOverNode.addGameOverListener( new GameOverNode.GameOverListener() {
            public void newGamePressed() {
                model.newGame();
            }
        } );
    }

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
