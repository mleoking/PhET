
package edu.colorado.phet.quantumtunneling.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


/**
 * Tests the method ChartRenderingInfo.getPlotArea.
 * This method always returns an empty rectangle, which I believe is a bug.
 * It should return the same rectangle as ChartRenderingInfo.getPlotInfo().getPlotArea().
 * <p>
 * Sample output:
 * <code>
 * ChartRenderingInfo.getPlotArea() = java.awt.geom.Rectangle2D$Double[x=0.0,y=0.0,w=0.0,h=0.0]
 * PlotRenderingInfo.getPlotArea() = java.awt.geom.Rectangle2D$Double[x=8.0,y=4.0,w=484.0,h=492.0]
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestGetPlotArea extends ApplicationFrame {

    public TestGetPlotArea( String title ) {
        super( title );
        
        XYSeries series = new XYSeries( "Random Data" );
        series.add( 0, 0 );
        series.add( 1, 1 );
        XYSeriesCollection dataSet = new XYSeriesCollection( series );
        XYItemRenderer renderer = new StandardXYItemRenderer();
        XYPlot plot = new XYPlot( dataSet, null, null, renderer );
        JFreeChart chart = ChartFactory.createXYLineChart( null, null, null, 
                dataSet, PlotOrientation.HORIZONTAL, false, false ,false );
        final ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( new Dimension( 500, 500 ) );
       
        JButton button = new JButton( "Print plot area to System.out" );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                ChartRenderingInfo chartInfo = chartPanel.getChartRenderingInfo();
                System.out.println( "ChartRenderingInfo.getPlotArea() = " + chartInfo.getPlotArea() );
                PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
                System.out.println( "PlotRenderingInfo.getPlotArea() = " + plotInfo.getPlotArea() );
            }
        } );
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( button );
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        panel.add( chartPanel, BorderLayout.CENTER );
        panel.add( buttonPanel, BorderLayout.SOUTH );
        
        setContentPane( panel );
    }
    
    public static void main( String args[] ) {    
        TestGetPlotArea frame = new TestGetPlotArea( "test" );
        frame.pack();
        RefineryUtilities.centerFrameOnScreen( frame );
        frame.setVisible( true ); 
    }
}
