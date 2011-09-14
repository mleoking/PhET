// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.energyskatepark.basics.EnergySkateParkBasicsModule.CONTROL_FONT;
import static edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel.backgroundColor;

/**
 * Control panel that allows the user to change the track friction and stickiness (roller-coaster mode).
 *
 * @author Sam Reid
 */
public class TrackControlPanel extends PNode {

    public TrackControlPanel( final EnergySkateParkBasicsModule module, final EnergySkateParkSimulationPanel energySkateParkSimulationPanel, final PNode energyGraphControlPanel ) {
        addChild( new ControlPanelNode(
                new VBox( 10,

                          //Control box for track friction
                          new VBox( 0,
                                    new PhetPText( "Track Friction", CONTROL_FONT ),
                                    new OnOffPanel( module.frictionEnabled ),
                                    new PSwing( new TrackFrictionSlider( module ) ) ),

                          //Separator
                          new PhetPPath( new Line2D.Double( 0, 0, 100, 0 ) ),

                          //Control box for stickiness
                          new VBox( 0,
                                    new PhetPText( "Stick to Track", CONTROL_FONT ),
                                    new OnOffPanel( module.stickToTrack ) ) ),
                backgroundColor ) {{

            //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
            energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
                public void apply() {
                    setOffset( energySkateParkSimulationPanel.getWidth() - getFullBounds().getWidth() - EnergySkateParkBasicsModule.INSET, energyGraphControlPanel.getFullBounds().getMaxY() + EnergySkateParkBasicsModule.INSET );
                }
            } );
        }} );
    }
}