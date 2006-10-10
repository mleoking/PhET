/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 2, 2006
 * Time: 12:00:34 AM
 * Copyright (c) Jun 2, 2006 by Sam Reid
 */

public class ZoomPanel extends VerticalLayoutPanel {
    private JFreeChart chart;
    private EnergyPositionPlotCanvas energyPositionPlotCanvas;
    private double dy = 1000;
    private static final double MIN_SEP = 500;

    public ZoomPanel( final JFreeChart chart, EnergyPositionPlotCanvas energyPositionPlotCanvas ) {
        this.chart = chart;
        this.energyPositionPlotCanvas = energyPositionPlotCanvas;
        JButton comp = new JButton( EnergySkateParkStrings.getString( "zoom.out" ) );
        comp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                increase( +dy );
            }
        } );
        add( comp );
        JButton zoomIn = new JButton( EnergySkateParkStrings.getString( "zoom.in" ) );
        zoomIn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                increase( -dy );
            }
        } );
        add( zoomIn );
    }

    private void increase( double value ) {
        double lowerBound = chart.getXYPlot().getRangeAxis().getLowerBound();
        double upperBound = chart.getXYPlot().getRangeAxis().getUpperBound() + value;
        if( upperBound <= lowerBound + MIN_SEP ) {
            upperBound = lowerBound + MIN_SEP;
        }
        chart.getXYPlot().getRangeAxis().setRange( lowerBound, upperBound );
        energyPositionPlotCanvas.chartRangeChanged();
    }
}
