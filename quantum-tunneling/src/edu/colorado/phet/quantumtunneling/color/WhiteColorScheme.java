/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.color;

import java.awt.Color;


/**
 * WhiteColorScheme is a color scheme that features a white chart background.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WhiteColorScheme implements IColorScheme {
    
    public static final Color CHART_COLOR = Color.WHITE;
    public static final Color TICKS_COLOR = Color.BLACK;
    public static final Color GRIDLINES_COLOR = Color.DARK_GRAY;
    
    public static final Color REGION_MARKER_COLOR = Color.DARK_GRAY;
    
    public static final Color TOTAL_ENERGY_COLOR = Color.GREEN;
    public static final Color POTENTIAL_ENERGY_COLOR = new Color( 178, 25, 205 ); // purple
    
    public static final Color REAL_COLOR = Color.RED;
    public static final Color IMAGINARY_COLOR = Color.BLUE;
    public static final Color MAGNITUDE_COLOR = Color.BLACK;
    public static final Color PROBABILITY_DENSITY_COLOR = Color.BLACK;

    public static final Color DRAG_HANDLE_FILL_COLOR = new Color( 0, 0, 0, 0 ); // transparent
    public static final Color DRAG_HANDLE_STROKE_COLOR = new Color( 0, 0, 0, 140 );
    public static final Color DRAG_HANDLE_TEXT_COLOR = Color.BLACK;
      
    public WhiteColorScheme() {}
    
    public Color getChartColor() {
        return CHART_COLOR;
    }
    
    public Color getTicksColor() {
        return TICKS_COLOR;
    }
    
    public Color getGridlinesColor() {
        return GRIDLINES_COLOR;
    }
    
    public Color getRegionMarkerColor() { 
        return REGION_MARKER_COLOR;
    }
    
    public Color getTotalEnergyColor() {
        return TOTAL_ENERGY_COLOR;
    }
    
    public Color getPotentialEnergyColor() {
        return POTENTIAL_ENERGY_COLOR;
    }
    
    public Color getRealColor() {
        return REAL_COLOR;
    }
    
    public Color getImaginaryColor() {
        return IMAGINARY_COLOR;
    }
    
    public Color getMagnitudeColor() {
        return MAGNITUDE_COLOR;
    }
    
    public Color getProbabilityDensityColor() {
        return PROBABILITY_DENSITY_COLOR;
    }
    
    public Color getDragHandleFillColor() {
        return DRAG_HANDLE_FILL_COLOR;
    }

    public Color getDragHandleStrokeColor() {
        return DRAG_HANDLE_STROKE_COLOR;
    }
    
    public Color getDragHandleTextColor() {
        return DRAG_HANDLE_TEXT_COLOR;
    }
}
