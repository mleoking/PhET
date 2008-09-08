package edu.colorado.phet.common.piccolophet.test.testmacbug;

import java.awt.*;

import javax.swing.*;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Gradient Paint causes this program to crash on Mac OS 10.4.11 with Java 1.5.0_13.
 * The same program runs fine on Mac OS 10.5.4 with Java 1.5.0_13, so the problem
 * must be in native code on Mac OS 10.4.
 */
public class TestTabGraphic {
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        PCanvas pCanvas = new PCanvas();

        PPath tabNode = new PPath( new Rectangle( 20, 20 ) );
        tabNode.setPaint( new GradientPaint( 0, 0, Color.blue, 0, 10, Color.blue ) );

        pCanvas.getLayer().addChild( tabNode );
        frame.setContentPane( pCanvas );
        frame.setSize( 200, 200 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setVisible( true );
    }

}
