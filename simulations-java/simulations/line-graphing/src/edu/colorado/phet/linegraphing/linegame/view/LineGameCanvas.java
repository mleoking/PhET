// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.common.view.LGCanvas;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.GamePhase;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Line Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameCanvas extends LGCanvas {

    private final PNode settingsNode; // parent of the nodes related to choosing game settings
    private final PNode playNode; // parent of nodes related to playing the game
    private final PNode resultsNode; // parent of nodes related to displaying game results
    private final PNode rewardNode; // parent of nodes related to displaying rewards for exceptional scores

    public LineGameCanvas( LineGameModel model ) {

        // parent nodes for various "phases" of the game
        settingsNode = new PhetPNode();
        playNode = new PhetPNode();
        resultsNode = new PhetPNode();
        rewardNode = new PhetPNode();

        // set visibility of scenegraph branches based on what "phase" of the game we're in
        model.phase.addObserver( new VoidFunction1<GamePhase>() {
            public void apply( GamePhase gamePhase ) {
                settingsNode.setVisible( gamePhase == GamePhase.SETTINGS );
                playNode.setVisible( gamePhase == GamePhase.PLAY );
                resultsNode.setVisible( gamePhase == GamePhase.RESULTS );
                rewardNode.setVisible( gamePhase == GamePhase.REWARD );
            }
        } );
    }
}
