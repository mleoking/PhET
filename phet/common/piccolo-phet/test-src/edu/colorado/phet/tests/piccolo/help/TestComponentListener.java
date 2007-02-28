package edu.colorado.phet.tests.piccolo.help;

/**
 * User: Sam Reid
 * Date: Jun 2, 2006
 * Time: 2:12:25 PM
 * Copyright (c) Jun 2, 2006 by Sam Reid
 */

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class TestComponentListener {
    private JFrame frame;

    public TestComponentListener() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        JPanel pCanvas = new JPanel();
        pCanvas.addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                System.out.println( "TestMotionHelpBalloon.resized" );
            }

            public void componentShown( ComponentEvent e ) {
                System.out.println( "TestMotionHelpBalloon.componentShown" );
            }
        } );
        frame.setContentPane( pCanvas );
    }

    public static void main( String[] args ) {
        new TestComponentListener().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}

