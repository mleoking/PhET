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
}
