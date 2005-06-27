/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.util;



/**
 * FourierUtils is a collection of static utility methods.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierUtils {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Cache of sine values
    private static double[] _sineValues;
    
    // Cache of cosine values
    private static double[] _cosineValues;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /** Not intended for instantiation */
    private FourierUtils() {}
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /**
     * Approximates the sine of an angle.
     * Values are cached at 1-degree intervals.
     * The angle is converted to the closest integer degree in the range 0-355,
     * which is used to look up the value in the cache.
     * 
     * @param radians the angle, in radians
     * @return the sine
     */
    public static double sin( double radians ) {
        
        // Allocate the lookup table when needed.
        if ( _sineValues == null ) {
            _sineValues = new double[360];
            for ( int i = 0; i < 360; i++ ) {
                _sineValues[i] = Math.sin( Math.toRadians( i ) );
            }
        }
        
        // Look up the value.
        int index = (int) ( Math.round( Math.toDegrees( Math.abs( radians) ) ) % 360 );
        double value = _sineValues[ index ];
        
        // Use the correct sign.
        if ( radians < 0 ) {
            value = -value;
        }
        
        return value;
    }
    
    /**
     * Approximates the cosine of an angle.
     * Values are cached at 1-degree intervals.
     * The angle is converted to the closest integer degree in the range 0-355,
     * which is used to look up the value in the cache.
     * 
     * @param radians the angle, in radians
     * @return the sine
     */
    public static double cos( double radians ) {
        
        // Allocate the lookup table when needed.
        if ( _cosineValues == null ) {
            _cosineValues = new double[360];
            for ( int i = 0; i < 360; i++ ) {
                _cosineValues[i] = Math.cos( Math.toRadians( i ) );
            }
        }
        
        // Look up the value.
        int index = (int) ( Math.round( Math.toDegrees( Math.abs( radians) ) ) % 360 );
        double value = _cosineValues[ index ];
        
        return value;
    }
}
