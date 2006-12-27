/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

/**
 * User: Sam Reid
 * Date: Jun 9, 2005
 * Time: 4:20:11 PM
 * Copyright (c) Jun 9, 2005 by Sam Reid
 */

public class Complex {
    private double real;
    private double imag;

    public Complex( double real, double imag ) {
        this.real = real;
        this.imag = imag;
    }

    public Complex() {
        this( 0, 0 );
    }

    public void zero() {
        real = 0.0;
        imag = 0.0;
    }

    public String toString() {
        return "[" + real + "," + imag + "]";
    }

    public Complex times( double s ) {
        return new Complex( real * s, imag * s );
    }

    public Complex plus( Complex complex ) {
        return new Complex( real + complex.real, imag + complex.imag );
    }

    public Complex times( Complex a ) {
        return new Complex( a.real * real - a.imag * imag, a.real * imag + a.imag * real );
    }

    public Complex minus( Complex complex ) {
        return new Complex( real - complex.real, imag - complex.imag );
    }

    public Complex divideBy( Complex a ) {
        double q = a.real * a.real + a.imag * a.imag;
        double g = real * a.real + imag * a.imag;
        double h = imag * a.real - real * a.imag;
        return ( new Complex( g / q, h / q ) );
    }

    public double abs() {
        return ( Math.sqrt( real * real + imag * imag ) );
    }

    public double getReal() {
        return real;
    }

    public double getImaginary() {
        return imag;
    }

    public boolean isZero() {
        return real == 0 && imag == 0;
    }

    public boolean equals( Object obj ) {
        if( obj instanceof Complex ) {
            Complex c = (Complex)obj;
            return this.real == c.real && this.imag == c.imag;
        }
        return false;
    }

    public void setValue( Complex c ) {
        this.real = c.real;
        this.imag = c.imag;
    }

    public Complex complexConjugate() {
        return new Complex( real, imag );
    }
}
