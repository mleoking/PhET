// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import javax.swing.JButton;
import javax.swing.JFrame;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Author: Sam Reid
 * Mar 22, 2007, 7:04:47 PM
 */
public class TestDualPSwing {
    private JFrame frame;

    public TestDualPSwing() {
        frame = new JFrame();
        PSwingCanvas swingCanvas = new PSwingCanvas();
        frame.setContentPane( swingCanvas );
        JButton button1 = new JButton( "button" );
        PSwing child = new PSwing( button1 );
        System.out.println( "child.getBounds() = " + child.getBounds() );
        swingCanvas.getLayer().addChild( child );
        PSwing child2 = new PSwing( button1 );
        child2.setOffset( 100, 100 );
        swingCanvas.getLayer().addChild( child2 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 400, 400 );

    }

    public static void main( String[] args ) {
        new TestDualPSwing().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
