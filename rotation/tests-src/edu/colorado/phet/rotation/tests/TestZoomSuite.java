package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 10:10:09 PM
 * Copyright (c) Jan 1, 2007 by Sam Reid
 */

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.graphs.ZoomSuiteNode;

import javax.swing.*;

public class TestZoomSuite {
    private JFrame frame;

    public TestZoomSuite() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.addScreenChild( new ZoomSuiteNode() );
        frame.setContentPane( phetPCanvas );
    }

    public static void main( String[] args ) {
        new TestZoomSuite().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
