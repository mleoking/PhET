/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.tests.mna;

/**
 * User: Sam Reid
 * Date: Jun 15, 2006
 * Time: 3:37:10 PM
 * Copyright (c) Jun 15, 2006 by Sam Reid
 */

public class TestExample8 {
    public static void main( String[] args ) {
        double ds = 0.1;
        for( double s = 0; s < 10; s += ds ) {
            double value = -50 * s / ( 1 + 25 * s );
            System.out.println( value );
        }
    }
}
