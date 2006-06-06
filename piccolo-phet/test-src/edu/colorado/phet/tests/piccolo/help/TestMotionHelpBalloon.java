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

    public TestMotionHelpBalloon() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PCanvas pCanvas = new PCanvas();
        MotionHelpBalloon helpBalloon = new MotionHelpBalloon( pCanvas, "<html>Help!<br>Wiggle Me!</html>" );
        helpBalloon.animateTo( 500, 500 );
        pCanvas.getLayer().addChild( helpBalloon );
        helpBalloon.setOffset( 0, 0 );

        frame.setContentPane( pCanvas );
    }

    public static void main( String[] args ) {
        new TestMotionHelpBalloon().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}

