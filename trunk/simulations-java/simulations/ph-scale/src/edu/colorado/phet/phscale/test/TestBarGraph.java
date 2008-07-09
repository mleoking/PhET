/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * TestBarGraph tests the bar graph feature for ph-scale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestBarGraph extends JFrame {

    public TestBarGraph() {
        super( "TestBarGraph" );
        
        IntervalXYDataset dataset = createDataset();
        JFreeChart chart = createChart( dataset );
        ChartPanel chartPanel = new ChartPanel( chart );
        
        JLabel pHLabel = new JLabel( "pH:" );
        
        final JSlider pHSlider = new JSlider( -1, 15 );
        pHSlider.setValue( 7 );
        
        final JLabel pHValue = new JLabel( String.valueOf( pHSlider.getValue() ) );
        
        pHSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                pHValue.setText( String.valueOf( pHSlider.getValue() ) );
            }
        });
        
        JPanel controlPanel = new JPanel();
        EasyGridBagLayout controlPanelLayout = new EasyGridBagLayout( controlPanel );
        controlPanel.setLayout( controlPanelLayout );
        int row = 0;
        int column = 0;
        controlPanelLayout.addComponent( pHLabel, row, column++ );
        controlPanelLayout.addComponent( pHSlider, row, column++ );
        controlPanelLayout.addComponent( pHValue, row, column++ );
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( chartPanel, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.SOUTH );
        
        getContentPane().add( panel );
    }
    
    private static IntervalXYDataset createDataset() {
        
        TimeSeries series1 = new TimeSeries("Series 1", Day.class);
        series1.add(new Day(1, 1, 2003), 54.3);
        series1.add(new Day(2, 1, 2003), 20.3);
        series1.add(new Day(3, 1, 2003), 43.4);
        series1.add(new Day(4, 1, 2003), -12.0);

        TimeSeries series2 = new TimeSeries("Series 2", Day.class);
        series2.add(new Day(1, 1, 2003), 8.0);
        series2.add(new Day(2, 1, 2003), 16.0);
        series2.add(new Day(3, 1, 2003), 21.0);
        series2.add(new Day(4, 1, 2003), 5.0);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        return dataset;
    }
    
    /** 
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private static JFreeChart createChart( IntervalXYDataset dataset ) {
        
        JFreeChart chart = ChartFactory.createXYBarChart(
            "",      // chart title
            "X",  // domain (x) axis label
            true,
            "Y",     // range (y) axis label
            dataset,
            PlotOrientation.VERTICAL,
            false,  // legend
            false, // tooltips
            false // urls
        );

        XYPlot plot = (XYPlot) chart.getPlot(); 
        ClusteredXYBarRenderer r = new ClusteredXYBarRenderer();
        plot.setRenderer(r);
        return chart;        
    }
    
    public static void main( String args[] ) {
        TestBarGraph frame = new TestBarGraph();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 1024, 768 ) );
        frame.setVisible( true );
    }
}
