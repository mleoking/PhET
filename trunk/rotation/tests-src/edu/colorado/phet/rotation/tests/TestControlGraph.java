package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 9:05:50 AM
 * Copyright (c) Dec 29, 2006 by Sam Reid
 */

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.graphs.ControlGraph;
import edu.colorado.phet.rotation.model.SimulationVariable;

import javax.swing.*;

public class TestControlGraph {
    private JFrame frame;

    public TestControlGraph() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.addScreenChild( new ControlGraph( phetPCanvas, new SimulationVariable(), "title" ) );

        frame.setContentPane( phetPCanvas );
    }

    public static void main( String[] args ) {
        new TestControlGraph().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
