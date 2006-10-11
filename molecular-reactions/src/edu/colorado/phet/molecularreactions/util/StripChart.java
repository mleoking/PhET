package edu.colorado.phet.molecularreactions.util;/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.SwingClock;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

/**
 * edu.colorado.phet.molecularreactions.util.StripChart
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StripChart {

    private XYSeries[] series;
    private JFreeChart chart;
    private double xAxisRange;
    private int rangeDefSeriesNum;

    /**
     *
     * @param title
     * @param seriesNames
     * @param xAxisLabel
     * @param yAxisLabel
     * @param orienation
     * @param xAxisRange
     * @param minY
     * @param maxY
     */
    public StripChart( String title,
                       String[] seriesNames,
                       String xAxisLabel,
                       String yAxisLabel,
                       PlotOrientation orienation,
                       double xAxisRange,
                       double minY,
                       double maxY ) {
        this.xAxisRange = xAxisRange;

        series = new XYSeries[seriesNames.length + 1 ];
        XYSeriesCollection dataset = new XYSeriesCollection();
        for( int i = 0; i < series.length - 1; i++ ) {
            series[i] = new XYSeries( seriesNames[i] );
            dataset.addSeries( series[i] );
        }

        // Add an extra series that will make the plot the proper width
        // before any real data has been added to it
        rangeDefSeriesNum = series.length - 1;
        series[rangeDefSeriesNum] = new XYSeries( "range definition" );
        series[rangeDefSeriesNum].add( 0, minY );
        series[rangeDefSeriesNum].add( xAxisRange, maxY );
        dataset.addSeries( series[rangeDefSeriesNum] );

        chart = ChartFactory.createXYLineChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                orienation,
                true,
                true,
                false
        );

        XYPlot plot = (XYPlot)chart.getPlot();
//        plot.getDomainAxis().setRange( 0, xAxisRange );
        plot.getRangeAxis().setRange( minY, maxY );

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for( int i = 0; i < series.length; i++ ) {
            renderer.setSeriesLinesVisible( i, true );
            renderer.setSeriesShapesVisible( i, false );
        }
        renderer.setSeriesLinesVisible( series.length - 1, false );
        renderer.setSeriesShapesVisible( series.length - 1, false );
        renderer.setToolTipGenerator( new StandardXYToolTipGenerator() );
        renderer.setDefaultEntityRadius( 6 );
        plot.setRenderer( renderer );


    }

    public void addData( int seriesNum, double x, double y ) {
        series[seriesNum].add( x, y );
        while( x - series[seriesNum].getDataItem( 0 ).getX().doubleValue() > xAxisRange ) {
            series[seriesNum].remove( 0 );
        }

        // Clear away data in the range definition series
        if( seriesNum != rangeDefSeriesNum && series[rangeDefSeriesNum].getItemCount() > 0 ) {
            addData( rangeDefSeriesNum, x, y );
        }

        XYPlot plot = (XYPlot)chart.getPlot();
        plot.getDomainAxis().setRange( x - xAxisRange, x );

    }

    public JFreeChart getChart() {
        return chart;
    }


    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Strip Chart Test" );
        final StripChart stripChart = new StripChart( "Test Chart",
                                                      new String[]{"A", "B"},
                                                      "t",
                                                      "n",
                                                      PlotOrientation.VERTICAL,
                                                      10,
                                                      -4, 4 );
        ChartPanel chartPanel = new ChartPanel( stripChart.getChart() );
        chartPanel.setPreferredSize( new java.awt.Dimension( 500, 300 ) );
        frame.setContentPane( chartPanel );
        frame.pack();
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        SwingClock clock = new SwingClock( 40, .1 );
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                double t = clockEvent.getSimulationTime();
                stripChart.addData( 0, t, 3 * Math.sin( t ) );
                stripChart.addData( 1, t, 3 * Math.cos( t ) );
            }
        } );
        clock.start();

    }
}
