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
 * Module for the "Energy Skate Park Basics" Introduction
 *
 * @author Sam Reid
 */
public class IntroModule extends EnergySkateParkBasicsModule {
    private static final double INSET = 5;

    public IntroModule( PhetFrame phetFrame ) {
        super( "Introduction", phetFrame );

        //Add the control panel
        final ControlPanelNode controlPanelNode = new ControlPanelNode( new VBox(
                new PText( "Energy Graphs" ) {{setFont( new PhetFont( 16, true ) );}},
                new TextButtonNode( "Pie Chart" ),
                new TextButtonNode( "Bar Chart" ) ), EnergySkateParkLookAndFeel.backgroundColor ) {{

            //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
            energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
                public void apply() {
                    setOffset( energySkateParkSimulationPanel.getWidth() - getFullBounds().getWidth() - INSET, INSET );
                }
            } );
        }};
        energySkateParkSimulationPanel.getRootNode().addChild( controlPanelNode );

        //Add the "Reset all" button
        energySkateParkSimulationPanel.getRootNode().addChild( new TextButtonNode( "Reset All" ) {{

            //Set its location when the layout changes in the piccolo node, since this sim isn't using stage coordinates
            energySkateParkSimulationPanel.getRootNode().addLayoutListener( new VoidFunction0() {
                public void apply() {
                    setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + INSET * 2 );
                }
            } );
        }} );
    }
}