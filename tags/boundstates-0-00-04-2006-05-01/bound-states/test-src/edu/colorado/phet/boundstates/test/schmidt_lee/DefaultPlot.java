/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.test.schmidt_lee;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * User: Sam Reid
 * Date: Feb 15, 2006
 * Time: 7:51:32 PM
 * Copyright (c) Feb 15, 2006 by Sam Reid
 */

public class DefaultPlot extends ChartFrame {

    public DefaultPlot( String title, String x, String y ) {
        super( title, ChartFactory.createScatterPlot( title, x, y, new XYSeriesCollection( new XYSeries( "series", false, true ) ), PlotOrientation.VERTICAL, false, false, false ) );
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setSize( 400, 400 );
    }

    public void addData( double x, double y ) {
        addData( x, y, true );
    }

    public void addData( double x, double y, boolean notify ) {
        XYSeriesCollection dataset = (XYSeriesCollection)getChartPanel().getChart().getXYPlot().getDataset();
        dataset.getSeries( 0 ).add( x, y, notify );
    }

    public void setRenderer( XYItemRenderer renderer ) {
        getChartPanel().getChart().getXYPlot().setRenderer( renderer );
    }
}
