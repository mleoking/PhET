// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.math;

import fj.F;
import fj.F2;
import fj.data.List;
import fj.function.Integers;
import lombok.Data;

/**
 * Immutable fraction object with denominator and numerator, may be improper, unreduced, or have minus signs in both numerator and denominator.
 *
 * @author Sam Reid
 */
public @Data class Fraction {
    public final int numerator;
    public final int denominator;

    public boolean greaterThanOne() { return numerator > denominator; }

    public double toDouble() { return ( (double) numerator ) / denominator; }

    public String toString() { return numerator + "/" + denominator; }

    //Returns true of the fractions have the same numeric value (i.e. are reducible to the same value)
    public boolean approxEquals( final Fraction fractionValue ) {
        return Math.abs( toDouble() - fractionValue.toDouble() ) < 1E-6;
    }

    //Convenience constructor to make level declaration read a little easier
    public static Fraction fraction( int numerator, int denominator ) {
        return new Fraction( numerator, denominator );
    }

    //Return a reduced copy of this fraction.
    public Fraction reduce() {
        int gcd = gcd( numerator, denominator );
        return new Fraction( numerator / gcd, denominator / gcd );
    }

    //Get the greatest common denominator using the classic algorithm
    private int gcd( int a, int b ) { return b == 0 ? a : gcd( b, a % b ); }

    public Fraction times( final int scale ) {
        return new Fraction( numerator * scale, denominator * scale );
    }

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

    public Fraction plus( final Fraction fraction ) {
        return sum( List.list( this, fraction ) );
    }

    //Return true if this fraction is less than or equal to the provided fraction.
    public boolean lessThanOrEqualTo( final Fraction target ) {

        //Give them a common denominator
        Fraction a = new Fraction( this.numerator * target.denominator, this.denominator * target.denominator );
        Fraction b = new Fraction( target.numerator * this.denominator, target.denominator * this.denominator );
        return a.numerator <= b.numerator;
    }

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

    public static final F2<Fraction, Integer, Fraction> _times = new F2<Fraction, Integer, Fraction>() {
        @Override public Fraction f( final Fraction fraction, final Integer integer ) {
            return fraction.times( integer );
        }
    };

    //Sample test main
    public static void main( String[] args ) {
        System.out.println( "sum = " + sum( List.list( fraction( 1, 2 ), fraction( 2, 3 ), fraction( 1, 6 ) ) ) );
    }

    //Fractions are equal if they reduce to the same fraction.
    public boolean valueEquals( final Fraction b ) {
        return b.reduce().equals( this.reduce() );
    }
}