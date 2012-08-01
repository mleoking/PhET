// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.model;

import fj.F;
import fj.F2;
import fj.data.List;
import fj.function.Integers;
import lombok.Data;

/**
 * Immutable fraction object with denominator and numerator
 *
 * @author Sam Reid
 */
public @Data class Fraction {
    public final int numerator;
    public final int denominator;

    public double toDouble() { return ( (double) numerator ) / denominator; }

    public String toString() { return numerator + "/" + denominator; }

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

    public static final F<Fraction, Fraction> _reduce = new F<Fraction, Fraction>() {
        @Override public Fraction f( final Fraction fraction ) {
            return fraction.reduce();
        }
    };

    public Fraction times( final int scale ) { return new Fraction( numerator * scale, denominator * scale ); }

    public static final F2<Fraction, Integer, Fraction> _times = new F2<Fraction, Integer, Fraction>() {
        @Override public Fraction f( final Fraction fraction, final Integer integer ) {
            return fraction.times( integer );
        }
    };

    public static Fraction sum( final List<Fraction> values ) {

        //multiply all values together to get denominator
        final int denominator = values.foldLeft( new F2<Integer, Fraction, Integer>() {
            @Override public Integer f( final Integer integer, final Fraction fraction ) {
                return integer * fraction.denominator;
            }
        }, 1 );

        List<Integer> numerators = values.map( new F<Fraction, Integer>() {
            @Override public Integer f( final Fraction fraction ) {
                int scaleFactor = denominator / fraction.denominator;
                return fraction.numerator * scaleFactor;
            }
        } );

        Integer sum = numerators.foldLeft( Integers.add, 0 );
        return fraction( sum, denominator ).reduce();
    }

    public static void main( String[] args ) {
        System.out.println( "sum = " + sum( List.list( fraction( 1, 2 ), fraction( 2, 3 ), fraction( 1, 6 ) ) ) );
    }

    public Fraction plus( final int numerator, final int denominator ) { return plus( new Fraction( numerator, denominator ) ); }

    public Fraction plus( final Fraction fraction ) { return sum( List.list( this, fraction ) ); }

    public boolean leq( final Fraction target ) {
        return this.toDouble() <= target.toDouble();
    }
}