// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.games.GameOverNode.GameOverListener;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.GamePhase;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Portion of the scenegraph that corresponds to the "results" game phase
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ResultsNode extends PhetPNode {

    private RewardNode rewardNode;

    public ResultsNode( final LineGameModel model, final Dimension2D stageSize, final PhetPCanvas canvas ) {

        // show results when we enter this phase
        model.phase.addObserver( new VoidFunction1<GamePhase>() {
            public void apply( GamePhase phase ) {
                if ( phase == GamePhase.RESULTS ) {

                    // game reward, shown for perfect score
                    if ( model.isPerfectScore() ) {
                        rewardNode = new RewardNode();
                        rewardNode.setLevel( model.settings.level.get() );
                        addChild( rewardNode );
                        rewardNode.setRunning( true );
                    }

                    // game results
                    final GameOverNode gameOverNode = new GameOverNode( model.settings.level.get(),
                                                                 model.score.get(),
                                                                 model.getPerfectScore(),
                                                                 new DefaultDecimalFormat( "0" ),
                                                                 model.timer.time.get(),
                                                                 model.getBestTime( model.settings.level.get() ),
                                                                 model.isNewBestTime(),
                                                                 model.settings.timerEnabled.get() );
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
                    rewardNode = null;
                }
            }
        } );
    }

    // Sets the bounds of the reward node, called when the canvas is resized so that the reward fills the canvas.
    public void setRewardBounds( PBounds bounds ) {
        if ( rewardNode != null ) {
            rewardNode.setBounds( bounds );
        }
    }

    public boolean isRewardRunning() {
        return ( rewardNode != null && rewardNode.isRunning() );
    }

    public void setRewardRunning( boolean running ) {
        if ( rewardNode != null ) {
            rewardNode.setRunning( running );
        }
    }
}
