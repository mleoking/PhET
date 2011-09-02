// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Module for the "Energy Skate Park Basics" Friction tab
 *
 * @author Sam Reid
 */
public class FrictionModule extends EnergySkateParkBasicsModule {
    private ControlPanelNode trackFrictionControlPanel;

    public FrictionModule( PhetFrame phetFrame ) {
        super( "Friction", phetFrame );

        //Add a track friction control panel
        trackFrictionControlPanel = new ControlPanelNode( new VBox(
                new PText( "Track" ) {{setFont( new PhetFont( 16, true ) );}},
                new TextButtonNode( "Track Friction" ),
                new TextButtonNode( "Stick to Track" ) ), EnergySkateParkLookAndFeel.backgroundColor ) {{

            //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
            energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
                public void apply() {
                    setOffset( energySkateParkSimulationPanel.getWidth() - getFullBounds().getWidth() - INSET, energyGraphControlPanel.getFullBounds().getMaxY() + INSET );
                }
            } );
        }};
        energySkateParkSimulationPanel.getRootNode().addChild( trackFrictionControlPanel );

        addTrackSelectionControlPanel();
        addResetAllButton( trackFrictionControlPanel );

        load( PARABOLA, true );
    }
}