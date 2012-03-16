// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import static java.lang.Math.random;

/**
 * Utilities for creating random numbers
 *
 * @author Sam Reid
 */
public class RandomUtil {
    public static double randomAngle() {
        return random() * 2 * Math.PI;
    }
}
