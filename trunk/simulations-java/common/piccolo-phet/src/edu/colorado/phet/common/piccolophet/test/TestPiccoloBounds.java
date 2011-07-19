// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.common.piccolophet.test;

import javax.swing.JFrame;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 8:15:07 AM
 */

public class TestPiccoloBounds {
    static long lastElapsedTime = 0;
    static double totalAngle = 0;

    public static void main( String[] args ) {
        PCanvas pCanvas = new PCanvas();
        final PText pText = new PText( "Hello PhET\nTesting" );
        System.out.println( "pText.getWidth() = " + pText.getWidth() );
        System.out.println( "pText.getHeight() = " + pText.getHeight() );
        System.out.println( "pText.getBounds() = " + pText.getBounds() );


        PNode parent = new PNode();
        parent.addChild( pText );

        System.out.println( "parent.getWidth() = " + parent.getWidth() );
        System.out.println( "parent.getHeight() = " + parent.getHeight() );
        System.out.println( "parent.getBounds() = " + parent.getBounds() );
        System.out.println( "parent.getFullBounds() = " + parent.getFullBounds() );

        pCanvas.getLayer().addChild( pText );


        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( pCanvas );
        frame.setSize( 400, 600 );
        frame.setVisible( true );
    }
}
