/* Copyright 2004, Sam Reid */
package edu.colorado.phet.quantumtunneling.srr;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 8, 2006
 * Time: 10:06:00 PM
 * Copyright (c) Mar 8, 2006 by Sam Reid
 */

public class BasicPlotter {
    private JFreeChart chart;
    private ChartFrame chartFrame;
    private XYSeries xySeries;

    public BasicPlotter() {
        xySeries = new XYSeries( "0", false, true );
        chart = ChartFactory.createScatterPlot( "Title", "x", "y", new XYSeriesCollection( xySeries ), PlotOrientation.VERTICAL, false, false, false );

        chartFrame = new ChartFrame( "Chart", chart );
        chartFrame.setSize( 800, 600 );
        chart.getXYPlot().getRangeAxis().setRange( -1, 1 );
        chart.getXYPlot().setRenderer( new XYLineAndShapeRenderer( true, false ) );
        chartFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public void setVisible( boolean visible ) {
        chartFrame.setVisible( visible );
    }

    public void setData( Point2D.Double []objects ) {
        clearData();
        for( int i = 0; i < objects.length; i++ ) {
            Point2D.Double object = objects[i];
            xySeries.add( object.x, object.y, i == objects.length - 1 );
        }
        chartFrame.getContentPane().repaint();
    }

    private void clearData() {
        xySeries.clear();
    }
}
