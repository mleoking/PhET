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

import java.awt.geom.Point2D;
import java.text.DecimalFormat;


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
