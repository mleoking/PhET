// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.games.GameOverNode.GameOverListener;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.GamePhase;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Portion of the scenegraph that corresponds to the "results" game phase. (See GamePhase.RESULTS)
 * Displays a panel with the game results.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ResultsNode extends PhetPNode {

    private final RewardNode rewardNode;

    public ResultsNode( final LineGameModel model, final Dimension2D stageSize ) {

        rewardNode = new RewardNode();

        // show results when we enter this phase
        model.phase.addObserver( new VoidFunction1<GamePhase>() {
            public void apply( GamePhase phase ) {
                if ( phase == GamePhase.RESULTS ) {

                    // game reward, shown for perfect score
                    if ( model.isPerfectScore() ) {
                        rewardNode.setLevel( model.settings.level.get() );
                        addChild( rewardNode );
                        setRewardRunning( true );
                    }

                    // game results
                    final GameOverNode gameOverNode = new GameOverNode( model.settings.level.get(),
                                                                        model.results.score.get(),
                                                                        model.getPerfectScore(),
                                                                        new DefaultDecimalFormat( "0" ),
                                                                        model.timer.time.get(),
                                                                        model.results.getBestTime( model.settings.level.get() ),
                                                                        model.results.isNewBestTime,
                                                                        model.settings.timerEnabled.get(),
                                                                        LineGameConstants.BUTTON_COLOR );
                    gameOverNode.scale( 1.5 );
                    addChild( gameOverNode );
                    gameOverNode.setOffset( ( stageSize.getWidth() - gameOverNode.getFullBoundsReference().getWidth() ) / 2,
                                            ( stageSize.getHeight() - gameOverNode.getFullBoundsReference().getHeight() ) / 2 );

                    // change phase when "New Game" button is pressed
                    gameOverNode.addGameOverListener( new GameOverListener() {
                        public void newGamePressed() {
                            model.phase.set( GamePhase.SETTINGS );
                        }
                    } );
                }
                else {
                    removeAllChildren();
                    setRewardRunning( false );
                }
            }
        } );
    }

    // Sets the bounds of the reward node, called when the canvas is resized so that the reward fills the canvas.
    public void setRewardBounds( PBounds bounds ) {
        rewardNode.setBounds( bounds );
    }

    public boolean isRewardRunning() {
        return rewardNode.isRunning();
    }

    public void setRewardRunning( boolean running ) {
        rewardNode.setRunning( running );
    }
}
