// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test.help;

/**
 * User: Sam Reid
 * Date: Jun 2, 2006
 * Time: 2:12:25 PM
 *
 */

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.umd.cs.piccolo.PCanvas;

public class TestMotionHelpBalloon {
    private JFrame frame;
    private MotionHelpBalloon helpBalloon;

    public TestMotionHelpBalloon() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PCanvas pCanvas = new PCanvas();
        frame.setContentPane( pCanvas );

        helpBalloon = new MotionHelpBalloon( pCanvas, "<html>Help!<br>Wiggle Me!</html>" );

        pCanvas.getLayer().addChild( helpBalloon );
        helpBalloon.setOffset( 500, -20 );

        frame.setVisible( true );
        helpBalloon.animateTo( 250, 250 );
    }

    public static void main( String[] args ) {
        new TestMotionHelpBalloon();
    }
}

