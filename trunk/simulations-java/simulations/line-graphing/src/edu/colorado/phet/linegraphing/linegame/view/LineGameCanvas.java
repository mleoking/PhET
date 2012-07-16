// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.view.LGCanvas;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.GamePhase;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Line Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameCanvas extends LGCanvas  {

    private final PNode settingsNode; // parent of the nodes related to choosing game settings
    private final PNode playNode; // parent of nodes related to playing the game
    private final PNode resultsNode; // parent of nodes related to displaying game results
    private final GameAudioPlayer audioPlayer;

    public LineGameCanvas( final LineGameModel model ) {

        // parent nodes for various "phases" of the game
        settingsNode = new SettingsNode( model, getStageSize() );
        playNode = new PlayNode( model, getStageSize() );
        resultsNode = new ResultsNode( model, getStageSize() );

        // rendering order
        {
            addChild( resultsNode );
            addChild( playNode );
            addChild( settingsNode );
        }

        // audio
        audioPlayer = new GameAudioPlayer( model.settings.soundEnabled.get() );
        model.settings.soundEnabled.addObserver( new SimpleObserver() {
            public void update() {
                audioPlayer.setEnabled( model.settings.soundEnabled.get() );
            }
        } );

        // game "phase" changes
        model.phase.addObserver( new VoidFunction1<GamePhase>() {
            public void apply( GamePhase gamePhase ) {

                // visibility of scenegraph branches
                settingsNode.setVisible( gamePhase == GamePhase.SETTINGS );
                playNode.setVisible( gamePhase == GamePhase.PLAY );
                resultsNode.setVisible( gamePhase == GamePhase.RESULTS );

                // play audio when game ends
                if ( gamePhase == GamePhase.RESULTS ) {
                    if ( model.score.get() == model.getPerfectScore() ) {
                        audioPlayer.gameOverPerfectScore();
                    }
                    else {
                        audioPlayer.gameOverImperfectScore();
                    }
                }
            }
        } );
    }
}
