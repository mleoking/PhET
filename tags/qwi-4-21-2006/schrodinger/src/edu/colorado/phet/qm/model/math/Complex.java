/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.math;

/**
 * User: Sam Reid
 * Date: Jun 9, 2005
 * Time: 4:20:11 PM
 * Copyright (c) Jun 9, 2005 by Sam Reid
 */

public class Complex {
    public double real;
    public double imag;

    public Complex( double real, double imag ) {
        this.real = real;
        this.imag = imag;
    }

    public Complex() {
        this( 0, 0 );
    }

    public Complex( Complex complex ) {
        this( complex.real, complex.imag );
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

    public Complex plus( Complex c ) {
        return new Complex( real + c.real, imag + c.imag );
    }

    public Complex times( Complex a ) {
        return new Complex( a.real * real - a.imag * imag, a.real * imag + a.imag * real );
    }

    public Complex minus( Complex c ) {
        return new Complex( real - c.real, imag - c.imag );
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

    public boolean equals( Object obj ) {
        if( obj instanceof Complex ) {
            Complex c = (Complex)obj;
            return this.real == c.real && this.imag == c.imag;
        }
        return false;
    }

    public void setValue( double real, double imag ) {
        this.real = real;
        this.imag = imag;
    }

    public void setValue( Complex c ) {
        setValue( c.real, c.imag );
    }

    public Complex complexConjugate() {
        return new Complex( real, -imag );
    }

    public void scale( double scale ) {
        real *= scale;
        imag *= scale;
    }

    public Complex copy() {
        return new Complex( real, imag );
    }

    public void setToSum( Complex a, Complex b ) {
        real = a.real + b.real;
        imag = a.imag + b.imag;
    }

    public void setToProduct( Complex a, Complex b ) {
        real = a.real * b.real - a.imag * b.imag;
        imag = a.real * b.imag + a.imag * b.real;
    }

    public void setToSum( Complex aTemp, Complex bTemp, Complex cTemp ) {
        real = aTemp.real + bTemp.real + cTemp.real;
        imag = aTemp.imag + bTemp.imag + cTemp.imag;
    }

    public static Complex exponentiateImaginary( double theta ) {
        return new Complex( Math.cos( theta ), Math.sin( theta ) );
    }

    public void add( Complex val ) {
        add( val.real, val.imag );
    }

    public double getComplexPhase() {
        return Math.atan2( imag, real );
    }

    public void add( double real, double imag ) {
        this.real += real;
        this.imag += imag;
    }
}
