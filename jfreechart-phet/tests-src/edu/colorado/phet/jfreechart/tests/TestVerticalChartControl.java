package edu.colorado.phet.jfreechart.tests;

/**
 * Demonstrates usage and behavior of VerticalChartControl.
 * @author Sam Reid
 */

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.jfreechart.piccolo.VerticalChartControl;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PText;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestVerticalChartControl {
    private JFrame frame;
    int xIndex = 0;

    public TestVerticalChartControl() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

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
        final VerticalChartControl verticalChartControl = new VerticalChartControl( jFreeChartNode, new PText( "THUMB" ) );
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.addScreenChild( verticalChartControl );

        verticalChartControl.setOffset( 50, 50 );
        verticalChartControl.addListener( new VerticalChartControl.Listener() {
            public void valueChanged() {
                System.out.println( "verticalChartControl.getValue() = " + verticalChartControl.getValue() );
            }
        } );
        phetPCanvas.addScreenChild( new PhetPPath( verticalChartControl.getFullBounds(), new BasicStroke( 1 ), Color.blue ) );
        frame.setContentPane( phetPCanvas );

        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                series.add( xIndex, verticalChartControl.getValue() );
                xIndex++;
            }
        } );
        timer.start();
    }

    public static void main( String[] args ) {
        new TestVerticalChartControl().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
