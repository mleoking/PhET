// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Demonstrates the problem reported in Unfuddle #2015.
 * On Mac OS 10.6.2 with Java 1.6.0_17, the scenegraph's layout visibly changes when the canvas becomes visible.
 * The red square jumps from the upper-left to the center of the canvas.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestCanvasLayoutMacOS extends JFrame {

    public TestCanvasLayoutMacOS() {
        super( TestCanvasLayoutMacOS.class.getName() );
        setPreferredSize( new Dimension( 600, 400 ) );
        setContentPane( new TestCanvas() );
        pack();
    }

    private class TestCanvas extends PhetPCanvas {

        private final PPath pathNode;

        public TestCanvas() {
            super();
            // red square
            pathNode = new PPath( new Rectangle2D.Double( 0, 0, 200, 200 ) );
            pathNode.setPaint( Color.RED );
            getLayer().addChild( pathNode );
            updateLayout();
        }

        protected void updateLayout() {
            PDimension canvasSize = new PDimension( getWidth(), getHeight() );
            System.out.println( "updateLayout canvasSize=" + (int) canvasSize.width + "x" + (int) canvasSize.height );
            if ( canvasSize.getWidth() > 0 && canvasSize.getHeight() > 0 ) {
                // center in the canvas
                double x = ( canvasSize.getWidth() - pathNode.getFullBoundsReference().getWidth() ) / 2;
                double y = ( canvasSize.getHeight() - pathNode.getFullBoundsReference().getHeight() ) / 2;
                ;
                pathNode.setOffset( x, y );
            }
        }

    }

    private static class SleepThread extends Thread {

        public SleepThread( long millis ) {
            super( new Runnable() {
                public void run() {
                    while ( true ) {
                        try {
                            SwingUtilities.invokeAndWait( new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep( 1000 );
                                    }
                                    catch ( InterruptedException e ) {
                                        e.printStackTrace();
                                    }
                                }
                            } );
                        }
                        catch ( InterruptedException e ) {
                            e.printStackTrace();
                        }
                        catch ( InvocationTargetException e ) {
                            e.printStackTrace();
                        }
                    }
                }
            } );
        }
    }

    public static void main( String[] args ) {

        // This thread serves to make the problem more noticeable.
        new SleepThread( 1000 ).start();

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JFrame frame = new TestCanvasLayoutMacOS();
                frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
                SwingUtils.centerWindowOnScreen( frame );
                frame.setVisible( true );
            }
        } );
    }

}
