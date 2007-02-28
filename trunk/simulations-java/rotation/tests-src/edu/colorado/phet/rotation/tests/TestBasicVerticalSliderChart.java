package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 11:00:07 AM
 * Copyright (c) Jan 1, 2007 by Sam Reid
 */

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.graphs.combined.BasicVerticalSliderChart;
import edu.umd.cs.piccolo.nodes.PText;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class TestBasicVerticalSliderChart {
    private JFrame frame;

    public TestBasicVerticalSliderChart() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas phetPCanvas = new PhetPCanvas();
        frame.setContentPane( phetPCanvas );

        int xIndex = 0;
        final XYSeries series = new XYSeries( "series_1" );
        for( xIndex = 0; xIndex < 100; xIndex++ ) {
            series.add( xIndex, Math.sin( xIndex / 100.0 * Math.PI * 2 ) );
        }
        XYDataset dataset = new XYSeriesCollection( series );
        JFreeChart jFreeChart = ChartFactory.createXYLineChart( "title", "x", "y", dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChart.getXYPlot().getRangeAxis().setAutoRange( false );
        jFreeChart.getXYPlot().getRangeAxis().setRange( -2, 2 );
        JFreeChartNode jFreeChartNode = new JFreeChartNode( jFreeChart );
        jFreeChartNode.setBounds( 0, 0, 500, 400 );

        BasicVerticalSliderChart chart = new BasicVerticalSliderChart( jFreeChartNode, new PText( "Hello" ) );
        chart.setOffset( 100, 100 );
        phetPCanvas.addScreenChild( chart );
    }

    public static void main( String[] args ) {
        new TestBasicVerticalSliderChart().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
