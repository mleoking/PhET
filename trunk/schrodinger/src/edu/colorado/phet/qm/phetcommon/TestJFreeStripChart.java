/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

/**
 * User: Sam Reid
 * Date: Dec 20, 2005
 * Time: 12:34:48 AM
 * Copyright (c) Dec 20, 2005 by Sam Reid
 */

public class TestJFreeStripChart {
    public static void main( String[] args ) {

        XYSeriesCollection xyDataset = new XYSeriesCollection();
        final XYSeries series = new XYSeries( "0" );
        xyDataset.addSeries( series );
        for( int i = 0; i < 100; i++ ) {
            series.add( i, Math.sin( i / 100.0 * Math.PI * 2 ) );
        }
//        categoryDataset.
        JFreeChart plot = createChart( xyDataset );
        ChartFrame chartFrame = new ChartFrame( "ChartFrame", plot, false );

        chartFrame.pack();
        chartFrame.setVisible( true );


        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                series.add( i, Math.sin( i / 100.0 * Math.PI * 2 ) );
                i++;
                series.remove( 0 );
            }
        } );
        timer.start();
    }

    static int i = 100;

    private static JFreeChart createChart( XYDataset dataset ) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Legal & General Unit Trust Prices",  // title
                "Date",             // x-axis label
                "Price Per Unit",   // y-axis label
                dataset,            // data
                true,               // create legend?
                true,               // generate tooltips?
                false               // generate URLs?
        );

        XYPlot plot = (XYPlot)chart.getPlot();
        DateAxis axis = (DateAxis)plot.getDomainAxis();
//        axis.setDateFormatOverride( new SimpleDateFormat( "MMM-yyyy" ) );
        axis.setDateFormatOverride( new SimpleDateFormat( "ssss" ) );
        plot.setRangeGridlinesVisible( false );
        plot.setDomainGridlinesVisible( false );
        return chart;
    }
}
