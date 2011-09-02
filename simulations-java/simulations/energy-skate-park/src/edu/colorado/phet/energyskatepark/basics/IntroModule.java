// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Module for the "Energy Skate Park Basics" Introduction
 *
 * @author Sam Reid
 */
public class IntroModule extends EnergySkateParkBasicsModule {
    public IntroModule( PhetFrame phetFrame ) {
        super( "Introduction", phetFrame );

        ControlPanelNode controlPanelNode = new ControlPanelNode( new VBox(
                new PText( "Energy Graphs" ) {{setFont( new PhetFont( 16, true ) );}},
                new TextButtonNode( "Pie Chart" ),
                new TextButtonNode( "Bar Chart" )
        ) ) {{
//            setOffset( energySkateParkSimulationPanel. );
        }};

        energySkateParkSimulationPanel.getRootNode().addChild( controlPanelNode );
    }
}