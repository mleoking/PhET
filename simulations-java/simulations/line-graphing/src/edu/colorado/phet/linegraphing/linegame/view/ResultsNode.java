// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.games.GameOverNode.GameOverListener;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.GamePhase;

/**
 * Portion of the scenegraph that corresponds to the "results" game phase
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ResultsNode extends PhetPNode {

    public ResultsNode( final LineGameModel model, final Dimension2D stageSize ) {

        // show results when we enter this phase
        model.phase.addObserver( new VoidFunction1<GamePhase>() {
            public void apply( GamePhase phase ) {
                if ( phase == GamePhase.RESULTS ) {

                    // game reward, shown for perfect score
                    if ( model.score.get() == model.getPerfectScore() ) {
                        addChild( new RewardNode() {{
                            //TODO configure the reward for the game level
                            play();
                        }} );
                        //TODO adjust bounds of reward when canvas (or main frame) is resized
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
                    addChild( gameOverNode );
                    gameOverNode.setOffset( ( stageSize.getWidth() - gameOverNode.getFullBoundsReference().getWidth() ) / 2,
                               ( stageSize.getHeight() - gameOverNode.getFullBoundsReference().getHeight() ) / 2 );

                    gameOverNode.addGameOverListener( new GameOverListener() {
                        public void newGamePressed() {
                            model.phase.set( GamePhase.SETTINGS );
                        }
                    } );

                }
                else {
                    removeAllChildren();
                }
            }
        } );
    }
}
