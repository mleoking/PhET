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

import java.awt.Color;

import edu.colorado.phet.fourier.model.Harmonic;


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
    
    // Colors for harmonics
    public static Color[] _harmonicColors =
    {
            new Color( 1f, 0f, 0f ),
            new Color( 1f, 0.5f, 0f ),
            new Color( 1f, 1f, 0f ),
            new Color( 0f, 1f, 0f ),
            new Color( 0f, 0.790002f, 0.340007f ),
            new Color( 0.392193f, 0.584307f, 0.929395f ),
            new Color( 0f, 0f, 1f ),
            new Color( 0f, 0f, 0.501999f ),
            new Color( 0.569994f, 0.129994f, 0.61999f ),
            new Color( 0.729408f, 0.333293f, 0.827494f ),
            new Color( 1f, 0.411802f, 0.705893f )
    };
    
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
    
    public static int getNumberOfHarmonicColors() {
        return _harmonicColors.length;
    }
    
    public static void setHarmonicColor( int n, Color color ) {
        //XXX synchronization necessary?
        _harmonicColors[ n ] = color;
    }
    
    /**
     * Gets the color that corresponds to a specified harmonic.
     * 
     * @param n the harmonic number, starting from zero
     * @throws IllegalArgumentException if n is out of range
     */
    public static Color getHarmonicColor( int n ) {
      if ( n < 0 || n >= _harmonicColors.length ) {
          throw new IllegalArgumentException( "n is out of range: " + n );
      }
      return _harmonicColors[ n ];
    }
    
    /**
     * Gets the color that corresponds to a specified harmonic.
     * 
     * @param Harmonic the harmonic
     * @throws IllegalArgumentException if n is out of range
     */
    public static Color getHarmonicColor( Harmonic harmonic ) {
        return getHarmonicColor( harmonic.getOrder() );
    }
    
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
        if ( _sineValues == null ) {
            _sineValues = new double[360];
            for ( int i = 0; i < 360; i++ ) {
                _sineValues[i] = Math.sin( Math.toRadians( i ) );
            }
        }
        int index = (int) ( Math.round( Math.toDegrees( radians) ) % 360 );
        return _sineValues[ index ];
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
        if ( _cosineValues == null ) {
            _cosineValues = new double[360];
            for ( int i = 0; i < 360; i++ ) {
                _cosineValues[i] = Math.cos( Math.toRadians( i ) );
            }
        }
        int index = (int) ( Math.round( Math.toDegrees( radians) ) % 360 );
        return _cosineValues[ index ];
    }
}
