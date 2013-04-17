// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.tests;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 10:10:09 PM
 *
 */

import javax.swing.JFrame;

import edu.colorado.phet.common.motion.graphs.ZoomSuiteNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

public class TestZoomSuite {
    private JFrame frame;

    public TestZoomSuite() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas phetPCanvas = new PhetPCanvas();
        ZoomSuiteNode suiteNode = new ZoomSuiteNode();
        suiteNode.setHorizontalZoomOutEnabled( false );
        phetPCanvas.addScreenChild( suiteNode );
        frame.setContentPane( phetPCanvas );
    }

    public static void main( String[] args ) {
        new TestZoomSuite().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
