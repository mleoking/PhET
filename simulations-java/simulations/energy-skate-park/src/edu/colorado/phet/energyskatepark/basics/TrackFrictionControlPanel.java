// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Control panel that allows the user to change the track friction and stickiness (roller-coaster mode).
 *
 * @author Sam Reid
 */
public class TrackFrictionControlPanel extends PNode {
    public TrackFrictionControlPanel( final EnergySkateParkSimulationPanel energySkateParkSimulationPanel, final PNode energyGraphControlPanel ) {
        addChild( new ControlPanelNode( new VBox(
                new PText( "Track" ) {{setFont( new PhetFont( 16, true ) );}},
                new TextButtonNode( "Track Friction" ),
                new TextButtonNode( "Stick to Track" ) ), EnergySkateParkLookAndFeel.backgroundColor ) {{

            //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
            energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
                public void apply() {
                    setOffset( energySkateParkSimulationPanel.getWidth() - getFullBounds().getWidth() - EnergySkateParkBasicsModule.INSET, energyGraphControlPanel.getFullBounds().getMaxY() + EnergySkateParkBasicsModule.INSET );
                }
            } );
        }} );
    }
}