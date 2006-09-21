/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.util.WIStrings;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 14, 2006
 * Time: 10:09:01 AM
 * Copyright (c) Apr 14, 2006 by Sam Reid
 */

public class JFreeChartIcon {
    private XYDataset dataset;

    public JFreeChartIcon() {
        XYSeries series = new XYSeries( "" );
        for( int i = 0; i < 100; i++ ) {
            series.add( i, Math.sin( i / 10.0 ) );
        }
        dataset = new XYSeriesCollection( series );
        JFreeChart jFreeChart = ChartFactory.createXYLineChart( WIStrings.getString( "chart" ), "", "", dataset, PlotOrientation.VERTICAL, false, false, false );
//        jFreeChart.getXYPlot().getRangeAxis().setTickLabelsVisible( false );
//        jFreeChart.getXYPlot().getDomainAxis().setTickLabelsVisible( false );
        BasicStroke stroke = new BasicStroke( 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{5, 1}, 0 );
//        BasicStroke stroke = new BasicStroke( 5 );
        jFreeChart.getXYPlot().setRangeGridlineStroke( stroke );
        jFreeChart.getXYPlot().setRangeGridlinePaint( Color.black );
        jFreeChart.getXYPlot().setDomainGridlineStroke( stroke );
        jFreeChart.getXYPlot().setDomainGridlinePaint( Color.black );
        jFreeChart.setAntiAlias( true );
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( true, false );
        renderer.setPaint( Color.blue );
        renderer.setStroke( new BasicStroke( 10 ) );
//        renderer.set
        jFreeChart.getXYPlot().setRenderer( renderer );
        ChartFrame chartFrame = new ChartFrame( "", jFreeChart );
        chartFrame.setSize( 200, 200 );
        chartFrame.setVisible( true );
        chartFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public static void main( String[] args ) {
        new JFreeChartIcon().start();
    }

    private void start() {

    }
}
