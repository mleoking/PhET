/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.util;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * DebugUtils is a collection of static debugging utilities.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugUtils {

    public static final DecimalFormat DOUBLE_FORMATTER = new DecimalFormat( "0.00" );
    public static final DecimalFormat POINT_FORMATTER = new DecimalFormat( "0" );
    
    private DebugUtils() {}
    
    public static final String format( double d ) {
        return DOUBLE_FORMATTER.format( d );
    }
    
    public static final String format( Point2D p ) {
        return "[" + POINT_FORMATTER.format( p.getX() ) + "," + POINT_FORMATTER.format( p.getY() ) + "]";
    }
}
