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
 * TrigCache is a cache of trigonometric values.
 * It sacrifices precision for speed.
 * <p>
 * The first time that a trig function (sine, cosine) is called, a cache is 
 * built.  The cache contains values for the function at 1-degree intervals.
 * The value returned for a function is the closest value in the cache.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TrigCache {

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
    private TrigCache() {}
    
    //----------------------------------------------------------------------------
    // Cache access
    //----------------------------------------------------------------------------
    
    /**
     * Gets the approximate sine of some angle.
     * 
     * @param radians the angle, in radians
     * @return the sine
     */
    public static double sin( double radians ) {
        
        // Allocate the lookup table on the first request.
        if ( _sineValues == null ) {
            _sineValues = new double[ 360 ];
            for ( int i = 0; i < _sineValues.length; i++ ) {
                _sineValues[i] = Math.sin( Math.toRadians( i ) );
            }
        }
        
        // Find the closest integer to the requested angle.
        int index = (int) ( Math.round( Math.toDegrees( Math.abs( radians) ) ) % 360 );
        
        // Look up the value.
        double value = _sineValues[ index ];
        
        // Correct the value's sign.
        if ( radians < 0 ) {
            value = -value;
        }
        
        return value;
    }
    
    /**
     * Gets the approximate cosine of some angle.
     * 
     * @param radians the angle, in radians
     * @return the sine
     */
    public static double cos( double radians ) {
        
        // Allocate the lookup table on the first request.
        if ( _cosineValues == null ) {
            _cosineValues = new double[360];
            for ( int i = 0; i < 360; i++ ) {
                _cosineValues[i] = Math.cos( Math.toRadians( i ) );
            }
        }
        
        // Find the closest integer to the requested angle.
        int index = (int) ( Math.round( Math.toDegrees( Math.abs( radians) ) ) % 360 );
        
        // Look up the value.
        double value = _cosineValues[ index ];
        
        return value;
    }
}
