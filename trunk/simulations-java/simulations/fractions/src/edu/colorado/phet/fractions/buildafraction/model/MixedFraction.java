// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import fj.F;
import lombok.Data;

import edu.colorado.phet.fractions.buildafraction.model.numbers.NumberTarget;
import edu.colorado.phet.fractions.common.math.Fraction;

/**
 * A fraction representation that has a whole part, and a fraction part.  Like 1 3/4 (one and three fourths).  If the whole part is zero, it is not displayed.
 * To keep the code general, all fractions in Build a Fraction are MixedFractions, but in the first tab, the whole part is always zero
 * because mixed numbers are introduced in the 2nd tab.
 *
 * @author Sam Reid
 */
public @Data class MixedFraction {
    public final int whole;
    public final int numerator;
    public final int denominator;

    public static F<MixedFraction, Double> _toDouble = new F<MixedFraction, Double>() {
        @Override public Double f( final MixedFraction mixedFraction ) {
            return mixedFraction.toDouble();
        }
    };
    public static F<Fraction, MixedFraction> _toMixedFraction = new F<Fraction, MixedFraction>() {
        @Override public MixedFraction f( final Fraction fraction ) {
            return new MixedFraction( 0, fraction.numerator, fraction.denominator );
        }
    };
    public static final F<MixedFraction, Boolean> _greaterThanOne = new F<MixedFraction, Boolean>() {
        @Override public Boolean f( final MixedFraction f ) {
            return f.toFraction().greaterThanOne();
        }
    };
    public static F<MixedFraction, Integer> _numerator = new F<MixedFraction, Integer>() {
        @Override public Integer f( final MixedFraction mixedFraction ) {
            return mixedFraction.numerator;
        }
    };
    public static F<MixedFraction, Integer> _denominator = new F<MixedFraction, Integer>() {
        @Override public Integer f( final MixedFraction mixedFraction ) {
            return mixedFraction.denominator;
        }
    };
    public static F<MixedFraction, Integer> _whole = new F<MixedFraction, Integer>() {
        @Override public Integer f( final MixedFraction mixedFraction ) {
            return mixedFraction.whole;
        }
    };
    public static F<NumberTarget, Fraction> _fractionPart = new F<NumberTarget, Fraction>() {
        @Override public Fraction f( final NumberTarget numberTarget ) {
            return numberTarget.mixedFraction.getFractionPart();
        }
    };

    public static MixedFraction mixedFraction( final Integer whole, final Fraction fractionPart ) {
        return new MixedFraction( whole, fractionPart.numerator, fractionPart.denominator );
    }

    public Fraction getFractionPart() { return new Fraction( numerator, denominator ); }

    //Converts to a Fraction instance, but without reducing.  It is important to be non-reduced because some solutions are unreduced like 2/2
    public Fraction toFraction() {
        Fraction f = getFractionPart();
        return new Fraction( f.numerator + whole * f.denominator, f.denominator );
    }

    public double toDouble() { return whole + getFractionPart().toDouble(); }

    public boolean approxEquals( final Fraction value ) { return toFraction().approxEquals( value ); }

    public MixedFraction scaleNumeratorAndDenominator( final int scaleFactor ) {
        return new MixedFraction( whole, numerator * scaleFactor, denominator * scaleFactor );
    }

    public MixedFraction withReducedFractionPart() {
        Fraction reduced = getFractionPart().reduce();
        return new MixedFraction( whole, reduced.numerator, reduced.denominator );
    }

    public String toString() {
        return whole + " " + numerator + "/" + denominator;
    }
}