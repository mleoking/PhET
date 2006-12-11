/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.test.numerov;

import edu.colorado.phet.boundstates.test.benfold.Function;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 7:27:39 PM
 * Copyright (c) Mar 27, 2006 by Sam Reid
 */

public class TestNumerov {
    public static void main( String[] args ) {
        double length = 20;
        int numLatticePoints = 1000;
        double dx = numLatticePoints / length;
        Numerov numerov = new Numerov( new Function() {
            public double evaluate( double x ) {
                return x * x;
            }
        }, 1.0, 0.5, dx, 1.0 );
        double[]psi = new double[numLatticePoints];
        numerov.integrate( psi, 0, 1E-9, -length / 2 );
        for( int i = 0; i < psi.length; i++ ) {
            double v = psi[i];
            System.out.println( "psi[" + i + "] = " + v );
        }
    }
}
