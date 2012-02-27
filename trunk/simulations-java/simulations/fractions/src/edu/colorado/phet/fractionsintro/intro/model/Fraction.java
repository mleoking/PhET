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

    public static Fraction reduced( int num, int den ) {
        int value = num;
        if ( num > den ) { value = gcd( num, den ); }
        else if ( num < den ) { value = gcd( den, num ); }

        // set result based on common factor derived from gcd
        return new Fraction( num / value, den / value );
    }

    public static int gcd( int a, int b ) {
        int factor;
        while ( b != 0 ) {
            factor = b;
            b = a % b;
            a = factor;
        }
        return a;
    }

    public double getValue() {
        return (double) numerator / denominator;
    }
}