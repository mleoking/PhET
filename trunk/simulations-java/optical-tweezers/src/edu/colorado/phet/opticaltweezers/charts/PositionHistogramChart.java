/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;


public class PositionHistogramChart extends JFreeChart {

    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 255, 200 ); // translucent white
    private static final Color BORDER_COLOR = Color.BLACK;
    private static final Stroke BORDER_STROKE = new BasicStroke( 2f );
    
    private PositionHistogramPlot _plot;
    
    public PositionHistogramChart( PositionHistogramPlot plot ) {
        super( null /* title */, null /* titleFont */, plot, false /* createLegend */ );
        setAntiAlias( true );
        setBorderVisible( true );
        setBackgroundPaint( BACKGROUND_COLOR );
        setBorderPaint( BORDER_COLOR );
        setBorderStroke( BORDER_STROKE );
        _plot = plot;
    }
}
