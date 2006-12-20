/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.util;

import java.awt.Color;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;

/**
 * ColorUtils is a collection of utilities related to Color.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ColorUtils {

    /* Not intended for instantiation */
    private ColorUtils() {}
    
    /**
     * Converts a wavelength to a Color.
     * @param wavelength
     * @return
     */
    public static Color wavelengthToColor( double wavelength ) {
        Color color = null;
        if ( wavelength < VisibleColor.MIN_WAVELENGTH ) {
            color = HAConstants.UV_COLOR;
        }
        else if ( wavelength > VisibleColor.MAX_WAVELENGTH ) {
            color = HAConstants.IR_COLOR;
        }
        else {
            color = VisibleColor.wavelengthToColor( wavelength );
        }
        return color;
    }
    
    /**
     * Interpolates between 2 colors in RGBA space.
     * When distance is 0, color1 is returned.
     * When distance is 1, color2 is returned.
     * Other values of distance return a color somewhere between color1 and color2.
     * Each color component is interpolated separately.
     * 
     * @param color1
     * @param color2
     * @param distance distance between color1 and color2, 0 <= distance <= 1
     * @return Color
     * @throws IllegalArgumentException if distance is out of range
     */
    public static Color interpolateRBGA( Color color1, Color color2, double distance ) {
        if ( distance < 0 || distance > 1 ) {
            throw new IllegalArgumentException( "distance out of range: " + distance );
        }
        int r = (int) interpolate( color1.getRed(), color2.getRed(), distance );
        int g = (int) interpolate( color1.getGreen(), color2.getGreen(), distance );
        int b = (int) interpolate( color1.getBlue(), color2.getBlue(), distance );
        int a = (int) interpolate( color1.getAlpha(), color2.getAlpha(), distance );
        return new Color( r, g, b, a );
    }
    
    /*
     * Interpolates between 2 values.
     * @param value1
     * @param value2
     * @param distance distance between value1 and value2, 0 <= distance <= 1
     * @return double, such that value1 <= double <= value2
     */
    private static double interpolate( double value1, double value2, double distance ) {
        assert ( distance >= 0 && distance <= 1 );
        return value1 + ( distance * ( value2 - value1 ) );
    }
}
