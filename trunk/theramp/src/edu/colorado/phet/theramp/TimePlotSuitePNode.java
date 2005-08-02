/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.colorado.phet.timeseries.TimeSeriesModelListenerAdapter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 2, 2005
 * Time: 2:17:06 PM
 * Copyright (c) Aug 2, 2005 by Sam Reid
 */

public class TimePlotSuitePNode extends PNode {
    private Range2D range;
    private TimeSeriesModel timeSeriesModel;

    public TimePlotSuitePNode( Range2D range, String name, TimeSeriesModel timeSeriesModel ) {
        this.range = range;
        this.timeSeriesModel = timeSeriesModel;
        XYDataset dataset = createDataset();
        JFreeChart chart = createChart( dataset, name );
        addChild( new PImage( chart.createBufferedImage( 800, 300 ) ) );

        timeSeriesModel.addListener( new TimeSeriesModelListenerAdapter() {

        } );
    }

    private static JFreeChart createChart( XYDataset dataset, String title ) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart( title,
                                                               "Time (seconds)", // x-axis label
                                                               "Energy", // y-axis label
                                                               dataset, // data
                                                               true, // create legend?
                                                               false, // generate tooltips?
                                                               false               // generate URLs?
        );

        chart.setBackgroundPaint( Color.lightGray );

        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinePaint( Color.gray );
        plot.setRangeGridlinePaint( Color.gray );
        plot.setAxisOffset( new RectangleInsets( 5.0, 5.0, 5.0, 5.0 ) );
        plot.setDomainCrosshairVisible( true );
        plot.setRangeCrosshairVisible( true );

        XYItemRenderer r = plot.getRenderer();
        if( r instanceof XYLineAndShapeRenderer ) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)r;
            renderer.setDefaultShapesVisible( false );
            renderer.setDefaultShapesFilled( false );
//            renderer.setBaseStroke( new BasicStroke( 2));
            renderer.setStroke( new BasicStroke( 3 ) );
        }

//        DateAxis axis = (DateAxis)plot.getDomainAxis();
//        axis.setDateFormatOverride( new SimpleDateFormat( "MMM-yyyy" ) );

        return chart;

    }

    /**
     * Creates a dataset, consisting of two series of monthly data.
     *
     * @return The dataset.
     */
    private static XYDataset createDataset() {

        TimeSeries s1 = new TimeSeries( "", Second.class );
        for( int i = 0; i < 100; i++ ) {
            Second sec = new Second( i * 2, 1, 1, 1, 1, 2005 );
            s1.add( sec, Math.sin( (double)i / 10.0 ) );
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries( s1 );
//        dataset.addSeries( s2 );

        dataset.setDomainIsPointsInTime( true );

        return dataset;

    }

    public void addTimeSeries( TimeSeriesPNode timeSeriesPNode ) {
    }

    public void reset() {
        System.out.println( "TODO" );
    }
}
