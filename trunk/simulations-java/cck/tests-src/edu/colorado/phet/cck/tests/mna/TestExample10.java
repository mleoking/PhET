/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.tests.mna;

/**
 * User: Sam Reid
 * Date: Jun 16, 2006
 * Time: 5:36:54 AM
 * Copyright (c) Jun 16, 2006 by Sam Reid
 */

public class TestExample10 {
    public static void main( String[] args ) {
        for( double s = 0; s < 100; s += 0.1 ) {
            double Vin = Math.cos( s );
            double val = -5 * s / ( 1 + 25 * s ) * Vin;
            System.out.println( val );
        }
    }
}
