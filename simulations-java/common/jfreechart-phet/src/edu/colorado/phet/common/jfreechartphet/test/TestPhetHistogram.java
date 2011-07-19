// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.jfreechartphet.test;

import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;

import edu.colorado.phet.common.jfreechartphet.PhetHistogramDataset;
import edu.colorado.phet.common.jfreechartphet.PhetHistogramSeries;

/**
 * Test application for PhetHistogramSeries and PhetHistogramDataset.
 * This test is not exhaustive, and the data is identical to the HistogramData1 demo in jfreechart.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPhetHistogram extends JFrame {

    public TestPhetHistogram() {
        super();

        // Series 1
        double[] values = new double[1000];
        Random generator = new Random( 12345678L );
        for ( int i = 0; i < 1000; i++ ) {
            values[i] = generator.nextGaussian() + 5;
        }
        PhetHistogramSeries series1 = new PhetHistogramSeries( "series1", 2.0, 8.0, 100, values );

        // Series 2
        values = new double[1000];
        for ( int i = 0; i < 1000; i++ ) {
            values[i] = generator.nextGaussian() + 7;
        }
        PhetHistogramSeries series2 = new PhetHistogramSeries( "series2", 4.0, 10.0, 100, values );

        // Dataset
        PhetHistogramDataset dataset = new PhetHistogramDataset();
        dataset.addSeries( series1 );
        dataset.addSeries( series2 );

        // Plot
        XYPlot plot = new XYPlot();
        plot.setDomainAxis( new NumberAxis( "position (meters)" ) );
        plot.setRangeAxis( new NumberAxis( "frequency" ) );
        plot.setDataset( dataset );

        // Renderer
        XYBarRenderer renderer = new XYBarRenderer();
        plot.setRenderer( renderer );

        // Chart
        JFreeChart chart = new JFreeChart( "PhetHistogram Test", plot );

        // Panel
        JPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 500, 270 ) );
        setContentPane( chartPanel );
    }

    public static void main( String[] args ) throws IOException {
        TestPhetHistogram testFrame = new TestPhetHistogram();
        testFrame.pack();
        testFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        testFrame.setVisible( true );
    }
}
