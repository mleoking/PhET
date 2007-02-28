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

import java.util.Random;

/**
 * RandomUtil contains utility methods related to random number generation.
 * These methods share a global random number generator, so they are 
 * not appropriate for all situations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class RandomUtils {

    /* Random number generator shared by all methods herein. */
    private static final Random RANDOM = new Random();
    
    /* Not intended for instantiation */
    private RandomUtils() {}
    
    /**
     * Gets a random value >= min and < max.
     * 
     * @param min
     * @param max
     * @return double
     */
    public static double nextDouble( double min, double max ) {
        assert( max > min );
        return min + ( RANDOM.nextDouble() * ( max - min ) );
    }
    
    /**
     * Gets a random boolean.
     * 
     * @return true or false
     */
    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }
    
    /**
     * Gets a random sign.
     * 
     * @return +1 or -1
     */
    public static int nextSign() {
        return ( RANDOM.nextBoolean() ? +1 : -1 );
    }
    
    /**
     * Gets a random angle >= 0 and < 2 * PI.
     * 
     * @return angle, in radians
     */
    public static double nextAngle() {
        return nextDouble( 0, 2 * Math.PI );
    }
}
