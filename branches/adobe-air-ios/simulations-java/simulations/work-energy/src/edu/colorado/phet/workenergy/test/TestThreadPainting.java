// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * @author Sam Reid
 */
public class TestThreadPainting {
    private JFrame frame;
    private double x;
    private Thread thread;
    private PhetPPath ptext;
    private boolean goingRight = true;

    public TestThreadPainting() {
        frame = new JFrame();
        final PhetPCanvas canvas = new PhetPCanvas();
        frame.setContentPane( canvas );
        ptext = new PhetPPath( new Ellipse2D.Double( 0, 0, 100, 100 ), Color.blue, new BasicStroke( 4 ), Color.red );
        canvas.addScreenChild( ptext );
        frame.setSize( 1024, 768 );
        thread = new Thread( new Runnable() {
            public void run() {
                while ( true ) {
                    long delayMilliseconds = 0;
//                    long delayMilliseconds = 0;
                    long startTime = System.nanoTime();

                    //TODO process swing inputs


                    updateModel();
                    long modelTime = System.nanoTime();
                    paintView();
                    long endTime = System.nanoTime();

                    long elapsedMilliseconds = ( endTime - startTime ) / 1000000L;
                    if ( elapsedMilliseconds < delayMilliseconds ) {
                        try {
                            Thread.sleep( delayMilliseconds - elapsedMilliseconds );
                        }
                        catch ( InterruptedException e ) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } );
    }

    private void paintView() {
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    JComponent jc = (JComponent) frame.getContentPane();
                    jc.paintImmediately( 0, 0, jc.getWidth(), jc.getHeight() );
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

    private void updateModel() {
        double speed = 0.5;
        if ( goingRight ) { x = x + speed; }
        else { x = x - speed; }
        if ( x > 1024 ) { goingRight = false; }
        if ( x < 0 ) { goingRight = true; }

        ptext.setOffset( x, 50 );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new TestThreadPainting().start();
            }
        } );
    }

    private void start() {
        frame.setVisible( true );
        thread.start();
    }
}
