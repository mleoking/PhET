/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.math.LinearTransform1D;

/**
 * User: Sam Reid
 * Date: Dec 12, 2004
 * Time: 8:24:00 PM
 * Copyright (c) Dec 12, 2004 by Sam Reid
 */

public class LinearTransformTest {

    public static void main( String[] args ) {
        LinearTransform1D map = new LinearTransform1D( -10, 10, 2, 3 );

        for( double d = map.getMinInput(); d <= map.getMaxInput(); d += .01 ) {
            double out = map.transform( d );
            System.out.println( "in = " + d + ", out=" + out );
        }
    }
}
