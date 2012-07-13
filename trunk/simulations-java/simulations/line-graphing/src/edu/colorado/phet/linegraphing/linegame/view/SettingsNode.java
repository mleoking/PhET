// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.GamePhase;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Portion of the scenegraph that corresponds to the "settings" game phase.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SettingsNode extends PhetPNode {

    public SettingsNode( final LineGameModel model, Dimension2D stageSize ) {

        // the standard game settings panel
        PNode panelNode = new PSwing( new GameSettingsPanel( model.settings, new VoidFunction0() {
            public void apply() {
                model.phase.set( GamePhase.PLAY );
            }
        } ) );
        panelNode.scale( 1.5 );
        addChild( panelNode );

        // centered on stage
        setOffset( ( stageSize.getWidth() - getFullBoundsReference().getWidth() ) / 2,
                   ( stageSize.getHeight() - getFullBoundsReference().getHeight() ) / 2 );
    }
}
