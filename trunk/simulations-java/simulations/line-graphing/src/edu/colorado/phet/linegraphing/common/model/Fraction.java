// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

/**
 * Data structure for a fraction (possibly improper).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Fraction {

    public final int numerator, denominator;

    public Fraction( int numerator, int denominator ) {
        this.numerator = numerator;
        this.denominator = denominator;
    }
}
