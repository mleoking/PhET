/* Copyright 2004, Sam Reid */
package edu.colorado.phet.jfreechart.tests;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.math.AbstractVector2D;

/**
 * User: Sam Reid
 * Date: Dec 14, 2005
 * Time: 4:50:58 PM
 * Copyright (c) Dec 14, 2005 by Sam Reid
 */

public class TestSplitRendering {
    private XYSeriesCollection dataset = new XYSeriesCollection();
    public ChartFrame chartFrame;
    public Timer timer;
    public XYSeries series;

    public TestSplitRendering() {
        series = new XYSeries( "Name" );
        dataset.addSeries( series );
        JFreeChart plot = ChartFactory.createScatterPlot( "Test Plot", "X-axis", "Y-axis", dataset, PlotOrientation.HORIZONTAL, true, false, false );
        XYPlot p= plot.getXYPlot();

        p.getDomainAxis().setRange( -1,1);
        p.getRangeAxis().setRange( -1,1);
        p.getDomainAxis().setAutoRange( false);
        p.getRangeAxis().setAutoRange( false);
        chartFrame = new ChartFrame( "Test Plot", plot );
        chartFrame.setDefaultCloseOperation( ChartFrame.EXIT_ON_CLOSE );
        chartFrame.pack();

        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                step();
            }

        } );
    }

    double startTime=System.currentTimeMillis()/1000.0;
    private void step() {
        double t = System.currentTimeMillis() / 1000.0-startTime;
        double r=Math.cos(t /60);
        double theta=Math.cos( t /5)*2*Math.PI;
        AbstractVector2D v=Vector2D.Double.parseAngleAndMagnitude( r,theta );
        series.add( v.getX(), v.getY());
    }

    public static void main( String[] args ) {
        new TestSplitRendering().start();
    }

    private void start() {
        chartFrame.setVisible( true );
        timer.start();
    }
}
