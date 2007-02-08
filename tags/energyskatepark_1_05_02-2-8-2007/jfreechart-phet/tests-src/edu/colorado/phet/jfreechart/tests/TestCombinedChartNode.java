package edu.colorado.phet.jfreechart.tests;

/**
 * See also: http://www.java2s.com/Code/Java/Chart/JFreeChartCombinedXYPlotDemo1.htm
 */

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class TestCombinedChartNode {
    private JFrame frame;

    public TestCombinedChartNode() {
        frame = new JFrame();
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        JFreeChartNode jFreeChartNode = new JFreeChartNode( createCombinedChart() );
        jFreeChartNode.setBounds( 0, 0, 700, 500 );

        jFreeChartNode.updateChartRenderingInfo();

        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.addScreenChild( jFreeChartNode );

        phetPCanvas.addScreenChild( new PhetPPath( jFreeChartNode.getDataArea( 0 ), new BasicStroke( 5 ), Color.blue ) );
        phetPCanvas.addScreenChild( new PhetPPath( jFreeChartNode.getDataArea( 1 ), new BasicStroke( 5 ), Color.red ) );

        frame.setContentPane( phetPCanvas );
    }

    private static JFreeChart createCombinedChart() {

        // create subplot 1...
        final XYItemRenderer renderer1 = new StandardXYItemRenderer();
        final NumberAxis rangeAxis1 = new NumberAxis( "Range 1" );
        final XYPlot subplot1 = new XYPlot( createDataset1(), null, rangeAxis1, renderer1 );
        subplot1.setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );

        final XYTextAnnotation annotation = new XYTextAnnotation( "Hello!", 50.0, 10000.0 );
        annotation.setFont( new Font( "SansSerif", Font.PLAIN, 9 ) );
        annotation.setRotationAngle( Math.PI / 4.0 );
        subplot1.addAnnotation( annotation );

        // create subplot 2...
        final XYItemRenderer renderer2 = new StandardXYItemRenderer();
        final NumberAxis rangeAxis2 = new NumberAxis( "Range 2" );
        rangeAxis2.setAutoRangeIncludesZero( false );
        final XYPlot subplot2 = new XYPlot( createDataset2(), null, rangeAxis2, renderer2 );
        subplot2.setRangeAxisLocation( AxisLocation.TOP_OR_LEFT );

        // parent plot...
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot( new NumberAxis( "Domain" ) );
        plot.setGap( 10.0 );

        // add the subplots...
        plot.add( subplot1, 1 );
        plot.add( subplot2, 1 );
        plot.setOrientation( PlotOrientation.VERTICAL );

        // return a new chart containing the overlaid plot...
        return new JFreeChart( "CombinedDomainXYPlot Demo",
                               JFreeChart.DEFAULT_TITLE_FONT, plot, true );

    }

    /**
     * Creates a sample dataset.
     *
     * @return Series 1.
     */
    private static XYDataset createDataset1() {

        // create dataset 1...
        final XYSeries series1 = new XYSeries( "Series 1" );
        series1.add( 10.0, 12353.3 );
        series1.add( 20.0, 13734.4 );
        series1.add( 30.0, 14525.3 );
        series1.add( 40.0, 13984.3 );
        series1.add( 50.0, 12999.4 );
        series1.add( 60.0, 14274.3 );
        series1.add( 70.0, 15943.5 );
        series1.add( 80.0, 14845.3 );
        series1.add( 90.0, 14645.4 );
        series1.add( 100.0, 16234.6 );
        series1.add( 110.0, 17232.3 );
        series1.add( 120.0, 14232.2 );
        series1.add( 130.0, 13102.2 );
        series1.add( 140.0, 14230.2 );
        series1.add( 150.0, 11235.2 );

        final XYSeries series2 = new XYSeries( "Series 2" );
        series2.add( 10.0, 15000.3 );
        series2.add( 20.0, 11000.4 );
        series2.add( 30.0, 17000.3 );
        series2.add( 40.0, 15000.3 );
        series2.add( 50.0, 14000.4 );
        series2.add( 60.0, 12000.3 );
        series2.add( 70.0, 11000.5 );
        series2.add( 80.0, 12000.3 );
        series2.add( 90.0, 13000.4 );
        series2.add( 100.0, 12000.6 );
        series2.add( 110.0, 13000.3 );
        series2.add( 120.0, 17000.2 );
        series2.add( 130.0, 18000.2 );
        series2.add( 140.0, 16000.2 );
        series2.add( 150.0, 17000.2 );

        final XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries( series1 );
        collection.addSeries( series2 );
        return collection;

    }

    /**
     * Creates a sample dataset.
     *
     * @return Series 2.
     */
    private static XYDataset createDataset2() {

        // create dataset 2...
        final XYSeries series2 = new XYSeries( "Series 3" );

        series2.add( 10.0, 16853.2 );
        series2.add( 20.0, 19642.3 );
        series2.add( 30.0, 18253.5 );
        series2.add( 40.0, 15352.3 );
        series2.add( 50.0, 13532.0 );
        series2.add( 100.0, 12635.3 );
        series2.add( 110.0, 13998.2 );
        series2.add( 120.0, 11943.2 );
        series2.add( 130.0, 16943.9 );
        series2.add( 140.0, 17843.2 );
        series2.add( 150.0, 16495.3 );
        series2.add( 160.0, 17943.6 );
        series2.add( 170.0, 18500.7 );
        series2.add( 180.0, 19595.9 );

        return new XYSeriesCollection( series2 );

    }

    public static void main( String[] args ) {
        new TestCombinedChartNode().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
    
