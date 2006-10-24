/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests.thirdparty.richardson;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 9:13:30 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */
class complex {
    double re, im;

    complex( double x, double y ) {
        re = x;
        im = y;
    }

    public void add( complex a, complex b ) {
        re = a.re + b.re;
        im = a.im + b.im;
    }

    public void mult( complex a, complex b ) {
        re = a.re * b.re - a.im * b.im;
        im = a.re * b.im + a.im * b.re;
    }

    public void set( complex a ) {
        re = a.re;
        im = a.im;
    }

    public void scale( double scale ) {
        re *= scale;
        im *= scale;
    }
}
