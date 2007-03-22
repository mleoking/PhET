/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.jfree.chart.JFreeChart;
import org.jfree.ui.RectangleInsets;

import edu.colorado.phet.common.view.util.SimStrings;


public class PotentialEnergyChart extends JFreeChart {

    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 255, 200 ); // translucent white
    private static final Color BORDER_COLOR = Color.BLACK;
    private static final Stroke BORDER_STROKE = new BasicStroke( 6f );
    private static final RectangleInsets INSETS = new RectangleInsets( 10, 10, 10, 10 ); // top,left,bottom,right
    
    private PotentialEnergyPlot _plot;
    
    public PotentialEnergyChart( PotentialEnergyPlot plot ) {
        super( SimStrings.get( "title.potentialEnergyChart" ), null /* titleFont */, plot, false /* createLegend */ );
        setAntiAlias( true );
        setBorderVisible( true );
        setBackgroundPaint( BACKGROUND_COLOR );
        setBorderPaint( BORDER_COLOR );
        setBorderStroke( BORDER_STROKE );
        setPadding( INSETS );
        _plot = plot;
    }
    
    public void clearData() {
        _plot.clearData();
    }
}
