/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Dec 20, 2005
 * Time: 12:58:10 AM
 * Copyright (c) Dec 20, 2005 by Sam Reid
 */

public class TestJFreeStripChartPNode {
    public static void main( String[] args ) {
        TestJFreeStripChart testJFreeStripChart = new TestJFreeStripChart();
        PSwingCanvas pCanvas = new PSwingCanvas();
        final JFreeChartNode jFreeChartNode = new JFreeChartNode( testJFreeStripChart.getjFreeChart() );
        jFreeChartNode.setBounds( new Rectangle2D.Double( 0, 0, 200, 200 ) );
        pCanvas.getLayer().addChild( jFreeChartNode );

        JFrame frame = new JFrame();
        frame.setContentPane( pCanvas );
        frame.setSize( 250, 250 );
        frame.setVisible( true );
        testJFreeStripChart.start();

//        Timer timer = new Timer( 30, new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                jFreeChartNode.chartChanged( new ChartChangeEvent( this ) );
//            }
//        } );
//        timer.start();
    }
}
