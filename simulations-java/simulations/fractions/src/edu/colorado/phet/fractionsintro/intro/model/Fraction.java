// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import lombok.Data;

/**
 * Immutable fraction object with denominator and numerator
 *
 * @author Sam Reid
 */
@Data public class Fraction {
    public final int numerator;
    public final int denominator;

    //Reduces a fraction
    public Fraction( int numerator, int denominator ) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public double toDouble() {
        return (double) numerator / denominator;
    }
}