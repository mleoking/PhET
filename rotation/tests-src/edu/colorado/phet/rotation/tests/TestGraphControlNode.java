package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:38:40 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.graphs.GraphControlNode;
import edu.colorado.phet.rotation.model.SimulationVariable;

import javax.swing.*;

public class TestGraphControlNode {
    private JFrame frame;

    public TestGraphControlNode() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.addScreenChild( new GraphControlNode( phetPCanvas, new SimulationVariable() ) );
        frame.setContentPane( phetPCanvas );
    }

    public static void main( String[] args ) {
        new TestGraphControlNode().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
