// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractions.common.math.Fraction;

import static edu.colorado.phet.fractions.common.math.Fraction.fraction;

/**
 * @author Sam Reid
 */
public @Data class MixedFraction {
    public final int whole;
    public final Fraction fraction;
    public static F<MixedFraction, Double> _toDouble = new F<MixedFraction, Double>() {
        @Override public Double f( final MixedFraction f ) {
            return f.toDouble();
        }
    };
    public static F<MixedFraction, Boolean> _greaterThanOne = new F<MixedFraction, Boolean>() {
        @Override public Boolean f( final MixedFraction f ) {
            return Fraction._greaterThanOne.f( f.toFraction() );
        }
    };
    public static F<Fraction, MixedFraction> _toMixedFraction = new F<Fraction, MixedFraction>() {
        @Override public MixedFraction f( final Fraction fraction ) {
            return new MixedFraction( 0, fraction );
        }
    };

    public Fraction toFraction() { return fraction.plus( fraction( whole, 1 ) ).reduce(); }

    public static MixedFraction mixedFraction( int whole, int numerator, int denominator ) {
        return new MixedFraction( whole, Fraction.fraction( numerator, denominator ) );
    }

    public double toDouble() { return toFraction().toDouble(); }
}