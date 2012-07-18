// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.view;

import fj.Effect;
import fj.data.List;

import java.text.DecimalFormat;

import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameModel;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameState;
import edu.colorado.phet.fractions.fractionmatcher.model.Mode;
import edu.colorado.phet.fractions.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * This class shows the graphics and provides user interaction for the matching game.
 * Even though using an immutable model, this class uses a more traditional piccolo approach for graphics,
 * creating nodes once and maintaining them and only updating them when necessary.
 *
 * @author Sam Reid
 */
public class MatchingGameCanvas extends AbstractFractionsCanvas {

    public MatchingGameCanvas( final boolean dev, final MatchingGameModel model, String title, final List<PNode> patterns ) {

        //Bar graph node that shows now bars, shown when the user has put something on both scales but hasn't checked the answer
        final PNode emptyBarGraphNode = new EmptyBarGraphNode();

        //Show the start screen when the user is choosing a level.
        addChild( new StartScreen( model, title, patterns ) );

        //Show the reward node behind the main node so it won't interfere with the results the user collected.
        addChild( new RewardNode( model ) );

        //Things to show during the game (i.e. not when settings dialog showing.)
        addChild( new GameNode( dev, model, emptyBarGraphNode, rootNode ) );

        //Show the game over dialog, if the game has ended.  Also, it has a stateful piccolo button so must not be cleared when the model changes, so it is stored in a field
        //and only regenerated when new games end.
        addChild( createGameOverDialog( model ) );
    }

    //Create the game over dialog, which is only shown when the game is over.
    private static UpdateNode createGameOverDialog( final MatchingGameModel model ) {
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
                                        model.state.set( model.state.get().newGame( state.info.level, state.info.score ) );
                                    }
                                } );
                            }} );
                        }
                    }
                }, model.mode );
    }

    public static VoidFunction1<Boolean> setNodeVisible( final PNode node ) {
        return new VoidFunction1<Boolean>() {
            public void apply( final Boolean visible ) {
                node.setVisible( visible );
            }
        };
    }
}