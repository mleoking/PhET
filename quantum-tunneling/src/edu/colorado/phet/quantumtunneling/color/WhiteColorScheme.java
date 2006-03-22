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
    
    private static final Color CHART_COLOR = Color.WHITE;
    private static final Color TICK_COLOR = Color.BLACK;
    private static final Color GRIDLINE_COLOR = Color.DARK_GRAY;
    private static final Color ANNOTATION_COLOR = Color.BLACK;
    
    private static final Color REGION_MARKER_COLOR = Color.BLACK;
    
    private static final Color TOTAL_ENERGY_COLOR = Color.GREEN;
    private static final Color POTENTIAL_ENERGY_COLOR = new Color( 178, 25, 205 ); // purple
    
    private static final Color REAL_COLOR = Color.RED;
    private static final Color IMAGINARY_COLOR = Color.BLUE;
    private static final Color MAGNITUDE_COLOR = Color.BLACK;
    private static final Color PROBABILITY_DENSITY_COLOR = Color.BLACK;
      
    public WhiteColorScheme() {}
    
    public Color getChartColor() {
        return CHART_COLOR;
    }
    
    public Color getTickColor() {
        return TICK_COLOR;
    }
    
    public Color getGridlineColor() {
        return GRIDLINE_COLOR;
    }
    
    public Color getAnnotationColor() {
        return ANNOTATION_COLOR;
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
}
