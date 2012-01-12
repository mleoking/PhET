// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import java.awt.*;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class OrderParameterVsTimeChart {
    private XYSeries meanSeries;
    private XYSeriesCollection xySeriesCollection;
    private NumberAxis domainAxis;
    private NumberAxis rangeAxis;
    private XYLineAndShapeRenderer renderer;
    private XYPlot xyPlot;
    private JFreeChart chart;
    private int maxItemCount;
    private boolean changedToScrolling = false;

    public OrderParameterVsTimeChart( int maxItemCount ) {
        this.maxItemCount = maxItemCount;

        meanSeries = new XYSeries( "Order Parameter vs Time" );
        xySeriesCollection = new XYSeriesCollection( meanSeries );
        domainAxis = new NumberAxis( "Time" );
        rangeAxis = new NumberAxis( "Order Parameter" );
        renderer = new XYLineAndShapeRenderer();

        int MEAN_SERIES = 0;
        renderer.setSeriesStroke( MEAN_SERIES, new BasicStroke( 2 ) );
        renderer.setSeriesLinesVisible( MEAN_SERIES, true );
        renderer.setSeriesShapesVisible( MEAN_SERIES, false );

        xyPlot = new XYPlot( xySeriesCollection,
                             domainAxis,
                             rangeAxis, renderer );
        chart = new JFreeChart( "Order Parameter vs. Time", xyPlot );
        rangeAxis.setRange( -0.01, 1.01 );
        domainAxis.setAutoRange( false );
        domainAxis.setRange( 0, maxItemCount * PlotOrderParameterVsTime.MOD );
    }

    public JFreeChart getChart() {
        return chart;
    }

    public void addDataPoint( double t, double orderParameter ) {
        meanSeries.add( t, orderParameter );
        while ( meanSeries.getItemCount() > maxItemCount ) {
            changeToScrolling();
            meanSeries.remove( 0 );

        }
        domainAxis.configure();
    }

    private void changeToScrolling() {
        if ( !changedToScrolling ) {
            changedToScrolling = true;
            domainAxis.setAutoRange( true );
            domainAxis.setAutoRangeIncludesZero( false );
        }
    }
}
