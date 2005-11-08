/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.plots.Range2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Nov 6, 2005
 * Time: 8:05:15 PM
 * Copyright (c) Nov 6, 2005 by Sam Reid
 */

public class EnergyPositionPlotPanelORIG extends JPanel {
    private JFreeChart chart;
    private XYSeriesCollection dataset;
    private ChartPanel chartPanel;
    private EC3Module module;

    private XYSeries peSeries;
    private XYSeries keSeries;
    private XYSeries totSeries;

    public EnergyPositionPlotPanelORIG( EC3Module ec3Module ) {
        this.module = ec3Module;
        ec3Module.getClock().addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                update();
            }
        } );
        dataset = createDataset();
        chart = createChart( new Range2D( 0, 0, 800, 400000 ), dataset, "Title" );
        chartPanel = new ChartPanel( chart );
        setLayout( new BorderLayout() );
        add( chartPanel, BorderLayout.CENTER );
        peSeries = new XYSeries( "Potential" );
        dataset.addSeries( peSeries );

        keSeries = new XYSeries( "Kinetic" );
        dataset.addSeries( keSeries );

        totSeries = new XYSeries( "Total" );
        dataset.addSeries( totSeries );
        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );
        add( clear, BorderLayout.SOUTH );
        chart.setAntiAlias( true );
    }

    private static JFreeChart createChart( Range2D range, XYDataset dataset, String title ) {
//        JFreeChart chart = ChartFactory.createXYLineChart( "",
        JFreeChart chart = ChartFactory.createScatterPlot( "",
                                                           "Position", // x-axis label
                                                           "Energy", // y-axis label
                                                           dataset, PlotOrientation.VERTICAL, true, true, false );

        chart.setBackgroundPaint( new Color( 240, 220, 210 ) );

        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinesVisible( true );
        plot.setRangeGridlinesVisible( false );

        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRange( true );
        xAxis.setRange( range.getMinX(), range.getMaxX() );
        xAxis.setTickLabelsVisible( false );
        xAxis.setTickMarksVisible( true );
        plot.setDomainAxis( xAxis );

        NumberAxis yAxis = new NumberAxis( title );
        yAxis.setAutoRange( true );
        yAxis.setRange( range.getMinY(), range.getMaxY() );
//        yAxis.setLabelFont( new Font( "Lucida Sans", Font.PLAIN, 11 ) );
        plot.setRangeAxis( yAxis );

        plot.setDomainCrosshairVisible( true );
        plot.setRangeCrosshairVisible( true );

        return chart;
    }

    private static XYSeriesCollection createDataset() {
        XYSeries xySeries = new XYSeries( new Integer( 0 ) );
        return new XYSeriesCollection( xySeries );
    }

    public void reset() {
        peSeries.clear();
        keSeries.clear();
        totSeries.clear();
    }

    private void update() {
        if( module.getEnergyConservationModel().numBodies() > 0 ) {
            Body body = module.getEnergyConservationModel().bodyAt( 0 );
            peSeries.add( body.getX(), module.getEnergyConservationModel().getPotentialEnergy( body ) );
            keSeries.add( body.getX(), body.getKineticEnergy() );
            totSeries.add( body.getX(), module.getEnergyConservationModel().getTotalEnergy( body ) );
        }
    }
}
