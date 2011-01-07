// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * @author Sam Reid
 */
public class TestSmoothAnimation {
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new JFrame() {{
                    setContentPane( new PhetPCanvas() {
                        long lastPaint = System.currentTimeMillis();


                        public void paintImmediately( int x, int y, int w, int h ) {
                            super.paintImmediately( x, y, w, h );
//                            System.out.println( "elaspedPaint = \t" + ( System.currentTimeMillis() - lastPaint ) );
                            lastPaint = System.currentTimeMillis();
                        }

                        {
                            setDoubleBuffered( false );
                            addScreenChild( new PhetPPath( new Ellipse2D.Double( 0, 0, 100, 100 ), Color.blue, new BasicStroke( 2 ), Color.black ) {{
                                final long[] lastTick = { System.currentTimeMillis() };
                                new Timer( 23, new ActionListener() {
                                    double velocity = 4;

                                    public void actionPerformed( ActionEvent e ) {
//                                    System.out.println("elapsed = "+(System.currentTimeMillis()- lastTick[0] ));
                                        translate( velocity, 0 );

                                        if ( getFullBoundsReference().getCenterX() > 800 ) {
                                            velocity = -Math.abs( velocity );
                                        }
                                        else if ( getFullBoundsReference().getCenterX() < 0 ) {
                                            velocity = Math.abs( velocity );
                                        }
                                        lastTick[0] = System.currentTimeMillis();
                                        paintImmediately();
                                    }
                                } ).start();
                            }} );
                        }
                    } );
                    setSize( 800, 600 );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }
}
