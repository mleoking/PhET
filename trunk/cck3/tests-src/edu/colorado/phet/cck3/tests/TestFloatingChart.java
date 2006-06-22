/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.tests;

/**
 * User: Sam Reid
 * Date: Jun 22, 2006
 * Time: 10:29:28 AM
 * Copyright (c) Jun 22, 2006 by Sam Reid
 */

import edu.colorado.phet.cck3.chart.FloatingChart;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;

public class TestFloatingChart {
    private JFrame frame;

    public TestFloatingChart() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PSwingCanvas contentPane = new PSwingCanvas();
        SwingClock clock = new SwingClock( 30, 1 );
        FloatingChart floatingChart = new FloatingChart( "floating chart test", new FloatingChart.ValueReader() {
            public double getValue( double x, double y ) {
                return Math.sin( x ) * y;
            }
        }, clock );
        contentPane.getLayer().addChild( floatingChart );
        frame.setContentPane( contentPane );
    }

    public static void main( String[] args ) {
        new TestFloatingChart().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}

