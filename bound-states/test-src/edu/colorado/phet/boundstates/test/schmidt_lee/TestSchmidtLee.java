/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.test.schmidt_lee;

import java.text.ParseException;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 11:23:20 PM
 * Copyright (c) Mar 27, 2006 by Sam Reid
 */

public class TestSchmidtLee {
    public static void main( String[] args ) throws BoundException, ParseException {
        Wavefunction wavefunction = new Wavefunction( 0.5, -10, 10, 1000, 0, new PotentialFunction() {
            public double evaluate( double x ) {
                return 0.5 * x * x;
            }
        } );
        System.out.println( "wavefunction.getE() = " + wavefunction.getE() );
    }
}
