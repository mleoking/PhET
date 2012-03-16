// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactionsandrates.view.charts;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

/**
 * edu.colorado.phet.molecularreactions.view.charts.BarChart
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BarChart implements ResizableChart {

    private JFreeChart chart;
    private BarRenderer renderer;
    private DefaultCategoryDataset dataset;

    public BarChart( String title,
                     String[] seriesNames,
                     String xAxisLabel,
                     String yAxisLabel,
                     PlotOrientation orientation,
                     int minY,
                     int maxY,
                     boolean showLegend ) {

        dataset = createDataset( seriesNames );

        // create the chart...
        chart = ChartFactory.createBarChart(
                title,                    // chart title
                xAxisLabel,               // domain axis label
                yAxisLabel,               // range axis label
                dataset,                  // data
                orientation,              // orientation
                showLegend,               // include legend
                true,                     // tooltips?
                false                     // URLs?
        );

        // set the background paint for the chart...
        chart.setBackgroundPaint( Color.white );

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();

        plot.getRangeAxis().setRange( minY, maxY );

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );

        // disable bar outlines...
        renderer = (BarRenderer)plot.getRenderer();
        renderer.setDrawBarOutline( false );

//        // set up gradient paints for series...
//        GradientPaint gp0 = new GradientPaint(
//                0.0f, 0.0f, Color.blue,
//                0.0f, 0.0f, new Color( 0, 0, 64 )
//        );
//        GradientPaint gp1 = new GradientPaint(
//                0.0f, 0.0f, Color.green,
//                0.0f, 0.0f, new Color( 0, 64, 0 )
//        );
//        GradientPaint gp2 = new GradientPaint(
//                0.0f, 0.0f, Color.red,
//                0.0f, 0.0f, new Color( 64, 0, 0 )
//        );
//        renderer.setSeriesPaint( 0, gp0 );
//        renderer.setSeriesPaint( 1, gp1 );
//        renderer.setSeriesPaint( 2, gp2 );
//
//        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setCategoryLabelPositions(
//                CategoryLabelPositions.createUpRotationLabelPositions( Math.PI / 6.0 )
//        );

    }

    private DefaultCategoryDataset createDataset( String[] seriesNames ) {

        // column keys...
        String category1 = "";

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for( int i = 0; i < seriesNames.length; i++ ) {
            dataset.setValue( i, seriesNames[i], category1 );
        }

        return dataset;
    }

    public void setSeriesPaint( int seriesNum, Paint paint ) {
        renderer.setSeriesPaint( seriesNum, paint );
    }

    public void addData( int value, String seriesName, String categoryName ) {
        dataset.setValue( value, seriesName, categoryName );
    }

    public JFreeChart getChart() {
        return chart;
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Bar Chart Test" );
        final BarChart barChart = new BarChart( "Test Chart",
                                                new String[]{"A", "BC", "AB", "C"},
                                                "Species",
                                                "n",
                                                PlotOrientation.VERTICAL,
                                                -12, 12, false );
        ChartPanel chartPanel = new ChartPanel( barChart.getChart() );
        chartPanel.setPreferredSize( new Dimension( 200, 200 ) );
        frame.setContentPane( chartPanel );
        frame.pack();
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        SwingClock clock = new SwingClock( 40, .1 );
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                double t = clockEvent.getSimulationTime();
                barChart.addData( (int)( 10 * Math.sin( t ) ), "A", "" );
                barChart.addData( (int)( 10 * Math.cos( t ) ), "B", "" );
            }
        } );
        clock.start();
    }

    public void setYRange( int minY, int maxY ) {
        getChart().getCategoryPlot().getRangeAxis().setRange( minY, maxY );
    }


    public Range getYRange() {
        return getChart().getCategoryPlot().getRangeAxis().getRange();
    }
}
