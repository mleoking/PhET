// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.tests.piccolo;

/**
 * Author: Sam Reid
 * Aug 13, 2007, 1:05:52 PM
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.QuickProfiler;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

public class PSwingStressTest {
    private JFrame frame = new JFrame( getClass().getName().substring( getClass().getName().lastIndexOf( '.' ) + 1 ) );

    public PSwingStressTest() {
        final MathUtil.Average average = new MathUtil.Average();
        final PSwingCanvas contentPane = new PSwingCanvas() {

            public void paintComponent( Graphics g ) {
                QuickProfiler profiler = new QuickProfiler( "paint" );
                super.paintComponent( g );

                average.addValue( profiler.getTime() );
                System.out.println( "numValues=" + average.numValues() + ", Avg: " + average.getAverage() + ", val=" + profiler.getTime() );
            }
        };
        PSwing layout = new PSwing( new JButton( "<html>Button<sub>" + 99 + ", " + 99 + "</sub></html>" ) );
        for ( int i = 0; i < 10; i++ ) {
            for ( int k = 0; k < 20; k++ ) {
                PSwing ps = new PSwing( new JButton( "<html>Button<sub>" + i + ", " + k + "</sub></html>" ) );
                contentPane.getLayer().addChild( ps );
                ps.setOffset( i * layout.getWidth(), k * layout.getHeight() );
            }

        }
        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
        Timer timer = new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                contentPane.paintImmediately( 0, 0, contentPane.getWidth(), contentPane.getHeight() );
            }
        } );
        timer.start();
    }

    private void start() {
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        new PSwingStressTest().start();
    }

    /**
     * Results from a run with different buffered image types:
     *
     numValues=100, Avg: 66.73, val=54 (opaque)
     numValues=100, Avg: 68.42, val=72 (bitmask)
     numValues=100, Avg: 68.82, val=76 (translucent)
     numValues=100, Avg: 71.3, val=65 (manual, ARGB_PRE)
     numValues=100, Avg: 66.39, val=72 (volatile, no attributes)
     */
}
