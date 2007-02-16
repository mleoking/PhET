package edu.colorado.phet.tests.piccolo.help;

/**
 * User: Sam Reid
 * Date: Jun 2, 2006
 * Time: 2:12:25 PM
 * Copyright (c) Jun 2, 2006 by Sam Reid
 */

import edu.colorado.phet.piccolo.help.MotionHelpBalloon;
import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;

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

