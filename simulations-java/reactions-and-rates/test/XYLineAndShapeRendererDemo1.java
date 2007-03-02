/* --------------------------------
 * XYLineAndShapeRendererDemo1.java
 * --------------------------------
 * (C) Copyright 2004, 2005, by Object Refinery Limited.
 *
 */

package demo;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;

/**
 * A simple demonstration of the {@link XYLineAndShapeRenderer} class.
 */
public class XYLineAndShapeRendererDemo1 extends ApplicationFrame {
    private static XYSeries series1;
    private static XYSeries series2;
    private static double x1;
    private static double x2;
    private static double rangeX = 5;

    /**
     * Constructs the demo application.
     *
     * @param title the frame title.
     */
    public XYLineAndShapeRendererDemo1( String title ) {

        super( title );
        final XYDataset dataset = createDataset();
        JFreeChart chart = createChart( dataset );
        ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 500, 300 ) );
        setContentPane( chartPanel );


        SwingClock clock = new SwingClock( 40, .1 );
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                double t = clockEvent.getSimulationTime();
                ++x1;
                ++x2;
                if( x1 > rangeX ) {
                    series1.remove( 0 );
                    series2.remove( 0 );
                }
                series1.add( x1, 3 * Math.sin( t ) );
                series2.add( x2, 3 * Math.cos( t ));
            }
        } );
        clock.start();

    }

    /**
     * Creates a sample chart.
     *
     * @param dataset a dataset for the chart.
     * @return A sample chart.
     */
    private static JFreeChart createChart( XYDataset dataset ) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "XYLineAndShapeRenderer Demo 1",
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        XYPlot plot = (XYPlot)chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible( 0, true );
        renderer.setSeriesShapesVisible( 0, false );
        renderer.setSeriesLinesVisible( 1, true );
        renderer.setSeriesShapesVisible( 1, false );
        renderer.setToolTipGenerator( new StandardXYToolTipGenerator() );
        renderer.setDefaultEntityRadius( 6 );
        plot.setRenderer( renderer );
        return chart;
    }

    /**
     * Creates a sample dataset.
     *
     * @return A dataset.
     */
    private static XYDataset createDataset() {
        x1 = 0;
        x2 = 0;
        series1 = new XYSeries( "Series 1" );
        series1.add( ++x1, 3.3 );
        series1.add( ++x1, 4.4 );
        series1.add( ++x1, 1.7 );
        series2 = new XYSeries( "Series 2" );
        series2.add( ++x2, 7.3 );
        series2.add( ++x2, 6.8 );
        series2.add( ++x2, 9.6 );
        series2.add( ++x2, 5.6 );
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( series1 );
        dataset.addSeries( series2 );
        return dataset;
    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart( createDataset() );
        return new ChartPanel( chart );
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args ignored.
     */
    public static void main( String[] args ) {

        XYLineAndShapeRendererDemo1 demo = new XYLineAndShapeRendererDemo1(
                "XYLineAndShapeRenderer Demo"
        );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen( demo );
        demo.setVisible( true );

    }
}
