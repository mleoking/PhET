// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.motionseries.javastage.stage.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PCanvas;

/**
 * It was pointed out that the motion of the block sliding on ice in the motion series sims wasn't
 * very smooth; this test is to try to provide a SSCCE (short self contained correct example) of the problems.
 *
 * @author Sam Reid
 */
public class SmoothMotionTest {
    private final JFrame frame;

    public SmoothMotionTest() {
        frame = new JFrame( getClass().getName() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        final PCanvas contentPane = new PCanvas();
        final PhetPPath node = new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 100 ), Color.yellow, new BasicStroke( 0.5f ), Color.black );
        contentPane.getLayer().addChild( node );

        final int delay = 25;
        final double VELOCITY = 5 / 30.0 * delay;//try to keep a nice velocity independent of the delay

        frame.setContentPane( contentPane );


        ( new Thread() {
            @Override
            public void run() {
                double velocity = VELOCITY;
                int count = 0;

                while ( true ) {
                    try {
                        Thread.sleep( delay );


//                if ( count % 5 == 0 ) {
//                    doWork();
//                    Runtime.getRuntime().gc();
//                }
                        doWork();
//                Runtime.getRuntime().gc();

                        if ( node.getFullBounds().getMaxX() > contentPane.getWidth() ) {
                            velocity = -VELOCITY;
                        }
                        else if ( node.getFullBounds().getMinX() < 0 ) {
                            velocity = VELOCITY;
                        }
                        node.setX( node.getX() + velocity );
                        node.setY( contentPane.getHeight() / 2 - node.getFullBounds().getHeight() / 2 );
//                        SwingUtilities.invokeLater( new Runnable() {
//                            public void run() {
//                                contentPane.paintImmediately( 0, 0, contentPane.getWidth(), contentPane.getHeight() );
//                            }
//                        } );

//                Runtime.getRuntime().gc();
                        count = count + 1;
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();


    }

    private void doWork() {
        StringBuffer string = new StringBuffer();
        for ( int i = 0; i < 20000; i++ ) {
//        for ( int i = 0; i < 100; i++ ) {
            Point2D.Double pt = new Point2D.Double( i, i / 2.0 );
            string.append( pt.toString() );
        }
        //Exception e = new Exception( string.toString() );
    }

    public static void main( String[] args ) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                new SmoothMotionTest().start();
            }
        } );
    }

    private void start() {
        frame.setVisible( true );
    }
}
