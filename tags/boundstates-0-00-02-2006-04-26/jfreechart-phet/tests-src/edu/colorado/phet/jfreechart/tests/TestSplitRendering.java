package edu.colorado.phet.jfreechart.tests;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * User: Sam Reid
 * Date: Dec 14, 2005
 * Time: 4:50:58 PM
 * Copyright (c) Dec 14, 2005 by Sam Reid
 */

public class TestSplitRendering {
    private XYSeriesCollection dataset = new XYSeriesCollection();
    private ChartFrame chartFrame;
    private Timer timer;
    private XYSeries series;
    private JFreeChart jFreeChart;

    public TestSplitRendering() {
        series = new XYSeries( "Name" );
        dataset.addSeries( series );
        jFreeChart = createScatterPlot( "Test Plot", "X-axis", "Y-axis", dataset, PlotOrientation.HORIZONTAL, true, false, false );
        final SplitXYPlot p = (SplitXYPlot)jFreeChart.getXYPlot();

        p.getDomainAxis().setRange( -1, 1 );
        p.getRangeAxis().setRange( -1, 1 );
        p.getDomainAxis().setAutoRange( false );
        p.getRangeAxis().setAutoRange( false );
        chartFrame = new ChartFrame( "Test Plot", jFreeChart );
        chartFrame.setDefaultCloseOperation( ChartFrame.EXIT_ON_CLOSE );
        chartFrame.pack();
        p.updateBuffer( jFreeChart, chartFrame.getContentPane().getWidth(), chartFrame.getContentPane().getHeight() );
        p.addChangeListener( new PlotChangeListener() {
            public void plotChanged( PlotChangeEvent event ) {
                if( event.getType() != ChartChangeEventType.DATASET_UPDATED ) {
                    System.out.println( "event = " + event );
                    p.updateBuffer( jFreeChart, chartFrame.getContentPane().getWidth(), chartFrame.getContentPane().getHeight() );
                }
            }
        } );
        chartFrame.addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                p.updateBuffer( jFreeChart, chartFrame.getWidth(), chartFrame.getHeight() );
            }

            public void componentShown( ComponentEvent e ) {
            }
        } );

        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                step();
            }
        } );

        JFrame controlFrame = new JFrame( "Controls" );
        controlFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        JPanel controlPanel = new JPanel();
        final JCheckBox backOff = new JCheckBox( "Quick Render" );
        backOff.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                p.drawBackgroundNoBuffer = !backOff.isSelected();
                p.setBackgroundIsBuffered( backOff.isSelected() );
            }
        } );
        controlPanel.add( backOff );
        controlFrame.setContentPane( controlPanel );
        controlFrame.pack();
        controlFrame.setVisible( true );
        controlFrame.setLocation( 0, chartFrame.getY() + chartFrame.getHeight() );
    }

    double startTime = System.currentTimeMillis() / 1000.0;

    private void step() {
        double t = System.currentTimeMillis() / 1000.0 - startTime;
        double r = Math.cos( t / 6 );
        double theta = Math.cos( t / 5 ) * 2 * Math.PI;
        AbstractVector2D v = Vector2D.Double.parseAngleAndMagnitude( r, theta );
        series.clear();
        series.add( v.getX(), v.getY() );
    }

    public static void main( String[] args ) {
        new TestSplitRendering().start();
    }

    private void start() {
        chartFrame.setVisible( true );
        timer.start();
    }

    public static JFreeChart createScatterPlot( String title,
                                                String xAxisLabel,
                                                String yAxisLabel,
                                                XYDataset dataset,
                                                PlotOrientation orientation,
                                                boolean legend,
                                                boolean tooltips,
                                                boolean urls ) {

        if( orientation == null ) {
            throw new IllegalArgumentException( "Null 'orientation' argument." );
        }
        NumberAxis xAxis = new NumberAxis( xAxisLabel );
        xAxis.setAutoRangeIncludesZero( false );
        NumberAxis yAxis = new NumberAxis( yAxisLabel );
        yAxis.setAutoRangeIncludesZero( false );

        SplitXYPlot plot = new SplitXYPlot( dataset, xAxis, yAxis, null );

        XYToolTipGenerator toolTipGenerator = null;
        if( tooltips ) {
            toolTipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if( urls ) {
            urlGenerator = new StandardXYURLGenerator();
        }
        XYItemRenderer renderer = new FastXYLineAndShapeRenderer( false, true );
        renderer.setBaseToolTipGenerator( toolTipGenerator );
        renderer.setURLGenerator( urlGenerator );
        plot.setRenderer( renderer );
        plot.setOrientation( orientation );

        JFreeChart chart = new JFreeChart(
                title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend
        );

        return chart;
    }

}
