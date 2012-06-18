// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F;
import fj.F2;
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

    //Convenience constructor to make level declaration read a little easier
    public static Fraction fraction( int numerator, int denominator ) { return new Fraction( numerator, denominator ); }

    public Fraction reduce() {
        int gcd = gcd( numerator, denominator );
        return new Fraction( numerator / gcd, denominator / gcd );
    }

    private int gcd( int a, int b ) { return b == 0 ? a : gcd( b, a % b ); }

    public static final F<Fraction, Integer> _denominator = new F<Fraction, Integer>() {
        @Override public Integer f( final Fraction fraction ) {
            return fraction.denominator;
        }
    };

    public static final F<Fraction, Integer> _numerator = new F<Fraction, Integer>() {
        @Override public Integer f( final Fraction fraction ) {
            return fraction.numerator;
        }
    };

    public Fraction times( final int scale ) { return new Fraction( numerator * scale, denominator * scale ); }

    public static final F2<Fraction, Integer, Fraction> _times = new F2<Fraction, Integer, Fraction>() {
        @Override public Fraction f( final Fraction fraction, final Integer integer ) {
            return fraction.times( integer );
        }
    };
}