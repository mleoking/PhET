// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jfreechartphet.test;

import java.awt.geom.Point2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;

/**
 * Author: Sam Reid
 * Jun 5, 2007, 7:52:28 PM
 */
public class TestJFreeChartNodeBuffer {
    public static void main( String[] args ) {
        XYSeries series = new XYSeries( "a" );
        series.add( 0, 0 );
        series.add( 10, 10 );
        XYDataset dataset = new XYSeriesCollection( series );
        JFreeChart chart = ChartFactory.createXYLineChart( "title", "x", "y", dataset, PlotOrientation.VERTICAL, false, true, true );
        JFreeChartNode jFreeChartNode = new JFreeChartNode( chart );
        jFreeChartNode.setBounds( 0, 0, 100, 100 );
        jFreeChartNode.setBuffered( false );
        System.out.println( "buffered=false" );
        System.out.println( "jFreeChartNode.plotToNode( new Point2D.Double( 0, 0 ) )=" + jFreeChartNode.plotToNode( new Point2D.Double( 0, 0 ) ) );
        System.out.println( "jFreeChartNode.plotToNode( new Point2D.Double( 5, 5 ) )=" + jFreeChartNode.plotToNode( new Point2D.Double( 5, 5 ) ) );
        System.out.println( "jFreeChartNode.getDataArea( ) = " + jFreeChartNode.getDataArea() );
        jFreeChartNode.setBuffered( true );
        System.out.println( "buffered=true" );
        System.out.println( "jFreeChartNode.plotToNode( new Point2D.Double( 0, 0 ) )=" + jFreeChartNode.plotToNode( new Point2D.Double( 0, 0 ) ) );
        System.out.println( "jFreeChartNode.plotToNode( new Point2D.Double( 5, 5 ) )=" + jFreeChartNode.plotToNode( new Point2D.Double( 5, 5 ) ) );
        System.out.println( "jFreeChartNode.getDataArea( ) = " + jFreeChartNode.getDataArea() );

    }
}
