// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.view;

import fj.Effect;

import java.text.DecimalFormat;

import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameModel;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameState;
import edu.colorado.phet.fractions.fractionmatcher.model.Mode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.STAGE_SIZE;

/**
 * @author Sam Reid
 */
class GameOverDialog {
    //Create the game over dialog, which is only shown when the game is over.
    public static UpdateNode createGameOverDialog( final MatchingGameModel model ) {
        return new UpdateNode(
                new Effect<PNode>() {
                    @Override public void e( final PNode parent ) {
                        parent.removeAllChildren();
                        if ( model.mode.get() == Mode.SHOWING_GAME_OVER_SCREEN ) {
                            final MatchingGameState state = model.state.get();
                            final int maxPoints = 12;
                            parent.addChild( new GameOverNode( state.info.level, state.info.score, maxPoints, new DecimalFormat( "0" ), state.info.time, state.info.bestTime, state.info.time >= state.info.bestTime, state.info.timerVisible ) {{
                                scale( 1.5 );
                                centerFullBoundsOnPoint( STAGE_SIZE.getWidth() / 2, STAGE_SIZE.getHeight() / 2 );
                                addGameOverListener( new GameOverListener() {
                                    public void newGamePressed() {

                                        //Refresh the level so the next time the user comes back they can play it again instead of seeing the "game over" dialog
                                        model.startNewGame();
                                    }
                                } );
                            }} );
                        }
                    }
                }, model.mode );
    }
}