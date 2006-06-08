/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.help;

import edu.colorado.phet.piccolo.help.MotionHelpBalloon;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestMotionHelpBalloonAnimateToNode {
    private JFrame frame;
    private MotionHelpBalloon helpBalloon;


    public TestMotionHelpBalloonAnimateToNode() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PCanvas pCanvas = new PCanvas();

        PUtil.DEFAULT_ACTIVITY_STEP_RATE = 1;
        PUtil.ACTIVITY_SCHEDULER_FRAME_DELAY = 10;

        final PNode dst = new PText( "Destination" );
        pCanvas.getLayer().addChild( dst );
        helpBalloon = new MotionHelpBalloon( pCanvas, "<html>Help!<br>Wiggle Me!</html>" );
        pCanvas.getLayer().addChild( helpBalloon );
        helpBalloon.setOffset( 0, 0 );

        frame.setContentPane( pCanvas );
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                double hz = 0.5;
                double angfreq = hz * 2 * Math.PI;
                dst.setOffset( 450, 400 + 100 * Math.sin( System.currentTimeMillis() / 1000.0 * angfreq ) );
            }
        } );

        frame.setVisible( true );
        timer.start();
        helpBalloon.animateTo( dst );
    }

    public static void main( String[] args ) {
        new TestMotionHelpBalloonAnimateToNode();
    }
}
