/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.test.benfold;

/**
 * Represents a simplified quadratic curve.  The function will have a
 * repeated root (on the origin by default).
 */
public class Quadratic implements Function {
    /**
     * Creates a new quadratic curve.
     *
     * @param coeff The <i>x</i>-squared coefficient
     * @param shift The <i>x</i>-coordinate of the root
     */
    public Quadratic( double coeff, double shift ) {
        this.coeff = coeff;
        this.shift = shift;
    }


    /**
     * Creates a new quadratic curve, with a single root on the origin.
     *
     * @param coeff The <i>x</i>-squared coefficient
     */
    public Quadratic( double coeff ) {
        this( coeff, Quadratic.DEFAULT_SHIFT );
    }


    /**
     * Creates a new quadratic curve, with a single root on the origin, and
     * passing through (-1,1) and (1,1).
     */
    public Quadratic() {
        this( Quadratic.DEFAULT_COEFF );
    }


    /**
     * @return coeff*(x-shift)*(x-shift)
     */
    public double evaluate( double x ) {
//        System.out.println( "x = " + x );
        x -= shift;
        return coeff * x * x;
    }


    public double getShift() {
        return shift;
    }

    public double getCoeff() {
        return coeff;
    }

    public void setShift( double d ) {
        shift = d;
    }

    public void setCoeff( double d ) {
        coeff = d;
    }


    protected double coeff, shift;
    public static final double
            DEFAULT_COEFF = 1,
            DEFAULT_SHIFT = 0;
}
