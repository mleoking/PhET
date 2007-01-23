package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Jan 22, 2007
 * Time: 4:22:38 PM
 * Copyright (c) Jan 22, 2007 by Sam Reid
 */

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import edu.umd.cs.piccolo.nodes.PText;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;

public class TestJFreeChartNodeCoordinates {
    private JFrame frame;
    private XYSeries series = new XYSeries( "a" );
    private XYDataset dataset = new XYSeriesCollection( series );
    private PhetPCanvas phetPCanvas;
    private JFreeChartNode jFreeChartNode;
    private PText zero;

    public TestJFreeChartNodeCoordinates() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        series.add( 0, 0 );
        series.add( 10, 2 );
        series.add( 20, -2 );
        JFreeChart chart = ChartFactory.createXYLineChart( "title", "x", "y", dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChartNode = new JFreeChartNode( chart );
        jFreeChartNode.setBuffered( true );

        phetPCanvas = new PhetPCanvas();
        phetPCanvas.addScreenChild( jFreeChartNode );

        frame.setContentPane( phetPCanvas );

        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        phetPCanvas.setPanEventHandler( new PPanEventHandler() );

        zero = new PText( "zero" );

//        phetPCanvas.addScreenChild( zero );
        jFreeChartNode.addChild( zero );
        relayout();
    }

    private void relayout() {
        jFreeChartNode.setOffset( 200, 200 );
        jFreeChartNode.setBounds( 0, 0, phetPCanvas.getWidth() - 200, phetPCanvas.getHeight() - 200 );
        jFreeChartNode.updateChartRenderingInfo();
        Point2D d = jFreeChartNode.plotToNode( new Point2D.Double( 0, 0 ) );
        zero.setOffset( d );
    }

    public static void main( String[] args ) {
        new TestJFreeChartNodeCoordinates().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
