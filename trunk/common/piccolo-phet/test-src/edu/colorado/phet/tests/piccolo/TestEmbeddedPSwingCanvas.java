package edu.colorado.phet.tests.piccolo;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;

/**
 * Author: Sam Reid
 * Mar 23, 2007, 11:29:29 AM
 */
public class TestEmbeddedPSwingCanvas {
    private JFrame frame;

    public TestEmbeddedPSwingCanvas() {
        PSwingCanvas embeddedCanvas = new PSwingCanvas();
        embeddedCanvas.getLayer().addChild( new PSwing( new JButton( "Button" ) ) );

        PSwingCanvas outerCanvas = new PSwingCanvas();
        outerCanvas.getLayer().addChild( new PSwing( embeddedCanvas ) );

        frame = new JFrame( "Test Embedded PSwingCanvas" );
        frame.setContentPane( outerCanvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 200, 200 );
    }

    public static void main( String[] args ) {
        new TestEmbeddedPSwingCanvas().start();

    }

    private void start() {
        frame.setVisible( true );
    }

}
