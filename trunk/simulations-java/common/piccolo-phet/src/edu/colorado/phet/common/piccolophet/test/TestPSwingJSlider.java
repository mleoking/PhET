package edu.colorado.phet.common.piccolophet.test;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sam Reid
 * Mar 20, 2007, 9:12:27 PM
 */
public class TestPSwingJSlider {
    private JFrame frame;

    public TestPSwingJSlider() {
        frame = new JFrame( "TestPSwingJSlider" );

        PSwingCanvas pCanvas = new PSwingCanvas();
        frame.setContentPane( pCanvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );

//        JComponent c2 = new JScrollBar();
        JComponent c2 = new JSlider();
//        c2.setPreferredSize(new Dimension( c2.getPreferredSize().width,400));
        PSwing child = new PSwing( c2 );
        child.setOffset( 100,100);
        pCanvas.getLayer().addChild( child );
        pCanvas.setPanEventHandler( null );
    }

    public static void main( String[] args ) {
        new TestPSwingJSlider().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
