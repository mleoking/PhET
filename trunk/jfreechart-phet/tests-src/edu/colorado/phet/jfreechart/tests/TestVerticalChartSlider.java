package edu.colorado.phet.jfreechart.tests;

/**
 * Demonstrates usage and behavior of VerticalChartControl.
 * @author Sam Reid
 */

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.jfreechart.piccolo.VerticalChartSlider;
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

public class TestVerticalChartSlider {
    private JFrame frame;
    int xIndex = 0;

    public TestVerticalChartSlider() {
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
        final VerticalChartSlider verticalChartSlider = new VerticalChartSlider( jFreeChartNode, new PText( "THUMB" ) );
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.addScreenChild( verticalChartSlider );

        verticalChartSlider.setOffset( 50, 50 );
        verticalChartSlider.addListener( new VerticalChartSlider.Listener() {
            public void valueChanged() {
                System.out.println( "verticalChartControl.getValue() = " + verticalChartSlider.getValue() );
            }
        } );
        phetPCanvas.addScreenChild( new PhetPPath( verticalChartSlider.getFullBounds(), new BasicStroke( 1 ), Color.blue ) );
        frame.setContentPane( phetPCanvas );

        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                series.add( xIndex, verticalChartSlider.getValue() );
                xIndex++;
            }
        } );
        timer.start();
    }

    public static void main( String[] args ) {
        new TestVerticalChartSlider().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
