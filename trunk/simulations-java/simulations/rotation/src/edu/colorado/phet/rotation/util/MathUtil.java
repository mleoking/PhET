// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.util;

/**
 * Author: Sam Reid
 * May 2, 2007, 1:16:02 AM
 */
public class MathUtil {
    public static double clampAngle( double angle, double min, double max ) {
        if ( max <= min ) {
            throw new IllegalArgumentException( "max<=min" );
        }
        while ( angle < min ) {
            angle += Math.PI * 2;
        }
        while ( angle > max ) {
            angle -= Math.PI * 2;
        }
        return angle;
    }
}
