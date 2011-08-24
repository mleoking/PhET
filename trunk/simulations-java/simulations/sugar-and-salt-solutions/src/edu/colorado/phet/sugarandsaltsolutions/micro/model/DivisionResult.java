// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * Result of dividing int by int, which has an int quotient and int remainder.  This class was created to help compare crystals to the given formula ratios (such as CaCl2)
 *
 * @author Sam Reid
 */
public class DivisionResult {
    public final int quotient;
    public final int remainder;

    public DivisionResult( int numerator, int denominator ) {
        this.quotient = numerator / denominator;
        this.remainder = numerator % denominator;
    }

    //IDEA-generated equals
    @Override public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        DivisionResult that = (DivisionResult) o;

        return quotient == that.quotient && remainder == that.remainder;
    }

    //IDEA-generated hashCode
    @Override public int hashCode() {
        int result = quotient;
        result = 31 * result + remainder;
        return result;
    }

    @Override public String toString() {
        return "DivisionResult{" + "quotient=" + quotient + ", remainder=" + remainder + '}';
    }
}