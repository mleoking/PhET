/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.tests;

/**
 * User: Sam Reid
 * Date: Jun 22, 2006
 * Time: 10:29:28 AM
 * Copyright (c) Jun 22, 2006 by Sam Reid
 */

import edu.colorado.phet.cck3.chart.AbstractFloatingChart;
import edu.colorado.phet.cck3.chart.SingleTerminalFloatingChart;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;

public class TestFloatingChart {
    private JFrame frame;
    private SwingClock clock;

    public TestFloatingChart() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PSwingCanvas contentPane = new PSwingCanvas();
        clock = new SwingClock( 30, 1 );
        AbstractFloatingChart floatingChart = new SingleTerminalFloatingChart( contentPane, "floating chart test", new AbstractFloatingChart.ValueReader() {
            public double getValue( double x, double y ) {
                double v = y;
//                System.out.println( "v = " + v );
                return v;
            }
        }, clock );
        floatingChart.setOffset( 100, 100 );
        contentPane.getLayer().addChild( floatingChart );
        contentPane.setPanEventHandler( null );
        frame.setContentPane( contentPane );
    }

    public static void main( String[] args ) {
        new TestFloatingChart().start();
    }

    private void start() {
        clock.start();
        frame.setVisible( true );
    }
}

