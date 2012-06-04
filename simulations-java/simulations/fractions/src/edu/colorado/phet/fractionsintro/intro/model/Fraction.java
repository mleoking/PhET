// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import lombok.Data;

/**
 * Immutable fraction object with denominator and numerator
 *
 * @author Sam Reid
 */
public @Data class Fraction {
    public final int numerator;
    public final int denominator;

    public double toDouble() {
        return (double) numerator / denominator;
    }

    //Returns true of the fractions have the same numeric value (i.e. are reducible to the same value)
    public boolean approxEquals( final Fraction fractionValue ) { return Math.abs( toDouble() - fractionValue.toDouble() ) < 1E-6; }
}