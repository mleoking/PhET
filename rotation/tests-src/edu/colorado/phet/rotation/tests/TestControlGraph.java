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
import edu.colorado.phet.rotation.util.BufferedPhetPCanvas;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TestControlGraph {
    private JFrame frame;
    private ControlGraph controlGraph;
    private PhetPCanvas phetPCanvas;

    public TestControlGraph() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        phetPCanvas = new BufferedPhetPCanvas();
        controlGraph = new ControlGraph( phetPCanvas, new SimulationVariable(), "abbrev", "title", -10, 10 );
        phetPCanvas.addScreenChild( controlGraph );
        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        frame.setContentPane( phetPCanvas );
        relayout();
    }

    private void relayout() {
        controlGraph.setBounds( 0, 0, phetPCanvas.getWidth(), phetPCanvas.getHeight() );
    }

    public static void main( String[] args ) {
        new TestControlGraph().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
