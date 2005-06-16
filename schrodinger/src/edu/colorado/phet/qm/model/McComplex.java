/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

/**
 * User: Sam Reid
 * Date: Jun 15, 2005
 * Time: 7:20:53 PM
 * Copyright (c) Jun 15, 2005 by Sam Reid
 */
public class McComplex {
    public double real;
    public double imaginary;

    public McComplex( double x, double y ) {
        real = x;
        imaginary = y;
    }

    public void setToSum( McComplex a, McComplex b ) {
        real = a.real + b.real;
        imaginary = a.imaginary + b.imaginary;
    }

    public void setToProduct( McComplex a, McComplex b ) {
        real = a.real * b.real - a.imaginary * b.imaginary;
        imaginary = a.real * b.imaginary + a.imaginary * b.real;
    }

    public void setValue( McComplex a ) {
        real = a.real;
        imaginary = a.imaginary;
    }


}
