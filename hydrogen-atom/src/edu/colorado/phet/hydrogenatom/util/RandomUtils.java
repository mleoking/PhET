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

/**
 * RandomUtil contains utility methods related to random number generation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class RandomUtils {

    /* Not intended for instantiation */
    private RandomUtils() {}
    
    /**
     * Gets a random value between min and max.
     * 
     * @param min
     * @param max
     * @return double
     */
    public static double nextDouble( double min, double max ) {
        assert( max > min );
        return min + ( Math.random() * ( max - min ) );
    }
    
    /**
     * Gets a random boolean.
     * 
     * @return true or false
     */
    public static boolean nextBoolean() {
        return ( Math.random() < 0.5 );
    }
    
    /**
     * Gets a random sign.
     * 
     * @return +1 or -1
     */
    public static int nextSign() {
        return ( ( Math.random() < 0.5 ) ? +1 : -1 );
    }
    
    /**
     * Gets a random orientation.
     * 
     * @return value in the range 0 to 2*PI radians
     */
    public static double nextOrientation() {
        return nextDouble( 0, 2 * Math.PI );
    }
}
