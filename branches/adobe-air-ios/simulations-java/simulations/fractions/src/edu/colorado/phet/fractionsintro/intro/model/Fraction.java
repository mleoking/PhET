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
}