// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;

/**
 * Upon completion of a Game, this node is used to display a summary of the user's game results.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALGameOverNode extends GameOverNode {
    
    private static final NumberFormat SCORE_FORMAT = new DecimalFormat( "0.#" );

    public RPALGameOverNode( final GameModel model ) {
        super( model.getLevel(), model.getPoints(), GameModel.getPerfectScore(), SCORE_FORMAT, model.getTime(), model.getBestTime(), model.isNewBestTime(), model.isTimerVisible() );
        addGameOverListener( new GameOverListener() {
            public void newGamePressed() {
                model.newGame();
            }
        } );
    }
}
