/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.tests.utils;

import edu.colorado.phet.common.math.MedianFilter;

/**
 * User: Sam Reid
 * Date: Dec 12, 2004
 * Time: 9:17:24 PM
 * Copyright (c) Dec 12, 2004 by Sam Reid
 */

public class TestMedianFilter {
    public static void main( String[] args ) {
        double[] d = new double[]{9, 8, 7, 6, 5, 6, 7, 4, 5, 8, 9};
        MedianFilter mf = new MedianFilter( d );
        d = mf.filter( 3 );
        for( int i = 0; i < d.length; i++ ) {
            System.out.println( d[i] );
        }
    }
}
