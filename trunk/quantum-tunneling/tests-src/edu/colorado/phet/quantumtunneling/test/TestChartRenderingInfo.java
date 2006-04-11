/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;


/**
 * TestChartRenderingInfo
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestChartRenderingInfo extends ApplicationFrame {

    private ChartPanel _chartPanel;
    
    public TestChartRenderingInfo( String title ) {
        super( title );
        
//        QTCombinedChart chart = new QTCombinedChart();
        XYSeries series = new XYSeries( "Random Data" );
        series.add( 0, 0 );
        series.add( 1, 1 );
        XYSeriesCollection dataSet = new XYSeriesCollection( series );
        XYItemRenderer renderer = new StandardXYItemRenderer();
        JFreeChart chart = ChartFactory.createXYLineChart( "My Chart", "X", "Y", 
                dataSet, PlotOrientation.HORIZONTAL, false, false ,false );
        _chartPanel = new ChartPanel( chart );
        _chartPanel.setPreferredSize( new Dimension( 500, 500 ) );
       
        JButton button = new JButton( "Print ChartRenderingInfo to System.out" );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                printChartRenderingInfo();
            }
        } );
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( button );
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        panel.add( _chartPanel, BorderLayout.CENTER );
        panel.add( buttonPanel, BorderLayout.SOUTH );
        
        setContentPane( panel );
    }
    
    private void printChartRenderingInfo() {
        ChartRenderingInfo chartInfo = _chartPanel.getChartRenderingInfo();
        System.out.println( "********************************************" );
        System.out.println( "ChartRenderingInfo:" );
        System.out.println( "  chartArea = " + chartInfo.getChartArea() );
        printPlotRenderingInfo( 0 /* depth */, 0 /* index */, chartInfo.getPlotInfo() );
        System.out.println( "********************************************" );
    }
    
    private void printPlotRenderingInfo( int depth, int index, PlotRenderingInfo plotInfo ) {
        System.out.println( "PlotRenderingInfo[" + index + "] @ depth " + depth + ":" );
        System.out.println( "  dataArea = " + plotInfo.getDataArea() );
        System.out.println( "  plotArea = " + plotInfo.getPlotArea() );
        System.out.println( "  subplotCount = " + plotInfo.getSubplotCount() );
        // Recursively print info for each subplot
        for ( int i = 0; i < plotInfo.getSubplotCount(); i++ ) {
            printPlotRenderingInfo( depth + 1, i, plotInfo.getSubplotInfo( i ) );
        }
    }

    public static void main( String args[] ) {
        SimStrings.init( args, QTConstants.LOCALIZATION_BUNDLE_BASENAME );
        
        TestChartRenderingInfo frame = new TestChartRenderingInfo( "ChartRenderingInfo test" );
        frame.pack();
        RefineryUtilities.centerFrameOnScreen( frame );
        frame.setVisible( true ); 
    }
}
