/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.umd.cs.piccolo.PNode;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 20, 2005
 * Time: 1:18:43 AM
 * Copyright (c) Dec 20, 2005 by Sam Reid
 */

public class StripChartJFCNode extends PNode {
    private XYSeries series;
    private JFreeChart jFreeChart;
    private JFreeChartNode jFreeChartNode;
    private boolean enabled = true;//debugging

    public StripChartJFCNode( int width, int height, String xAxis, String yAxis ) {
        XYSeriesCollection xyDataset = new XYSeriesCollection();
        series = new XYSeries( "time series" );
        xyDataset.addSeries( series );

        jFreeChart = createChart( xyDataset, xAxis, yAxis );
        jFreeChart.setBorderVisible( true );
        jFreeChart.setBorderStroke( new BasicStroke( 2 ) );
        jFreeChart.setBorderPaint( Color.gray );

        jFreeChartNode = new JFreeChartNode( jFreeChart );
        addChild( jFreeChartNode );
        jFreeChartNode.setBounds( 0, 0, width, height );
    }

    public static JFreeChart createChart( XYDataset dataset, String xaxis, String yaxis ) {
        JFreeChart chart = ChartFactory.createScatterPlot(
                "",  // title
                xaxis,             // x-axis label
                yaxis,   // y-axis label
                dataset,            // data
                PlotOrientation.VERTICAL,
                false,               // create legend?
                false,               // generate tooltips?
                false               // generate URLs?
        );

        XYPlot plot = (XYPlot)chart.getPlot();

        plot.getRangeAxis().setAutoRange( false );
        plot.getRangeAxis().setRange( -1, 1 );
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( true, false );
        renderer.setStroke( new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1 ) );
        renderer.setPaint( Color.blue );
        plot.setRenderer( renderer );
        return chart;
    }

    public void addValue( double x, double y ) {
        if( enabled ) {
            //todo can we temporarily disable render, do both steps as batch?
            series.add( x, y, series.getItemCount() < 100 );
            if( series.getItemCount() == 2 ) {
                double x0 = series.getX( 0 ).doubleValue();
                double dx = x - x0;
                double projectedMax = dx * 100 + x0;
                jFreeChart.getXYPlot().getDomainAxis().setRange( x0, projectedMax );
            }
            else if( series.getItemCount() == 100 ) {
                jFreeChart.getXYPlot().getDomainAxis().setAutoRange( true );
            }

            if( series.getItemCount() > 100 ) {
                series.remove( 0 );
            }
        }
    }
}
