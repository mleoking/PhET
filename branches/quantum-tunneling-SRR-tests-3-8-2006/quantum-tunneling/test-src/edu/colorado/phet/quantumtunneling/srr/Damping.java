/* Copyright 2004, Sam Reid */
package edu.colorado.phet.quantumtunneling.srr;

import edu.colorado.phet.quantumtunneling.util.LightweightComplex;

/**
 * User: Sam Reid
 * Date: Mar 10, 2006
 * Time: 10:26:09 PM
 * Copyright (c) Mar 10, 2006 by Sam Reid
 */

public class Damping {
    /* Each damping coefficient is applied to this many adjacent samples */
//    public static int SAMPLES_PER_DAMPING_COEFFICIENT = 20;
    public static int SAMPLES_PER_DAMPING_COEFFICIENT = 10;
    /* Damping coefficients, in order of application, starting from the boundaries of the sample space and working inward */
    public static double[] DAMPING_COEFFICIENTS = new double[]{0.001, 0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.15, 0.3, 0.5, 0.7, 0.85, 0.9, 0.925, 0.95, 0.975, 0.99, 0.995, 0.999};
//    public static double[] DAMPING_COEFFICIENTS;// = new double[]{0.001, 0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.15, 0.3, 0.5, 0.7, 0.85, 0.9, 0.925, 0.95, 0.975, 0.99, 0.995, 0.999,0.999};

//    static {
//        int dampCoeffSize = 50;
////        double x0 = dampCoeffSize / 2;
//        double x0 = 0;
//        double a = 1.0;
//        DAMPING_COEFFICIENTS = new double[dampCoeffSize];
//        for( int i = 0; i < DAMPING_COEFFICIENTS.length; i++ ) {
//            DAMPING_COEFFICIENTS[i] = 1.0 / ( 1.0 + Math.exp( -a * ( i - x0 ) ) );
//        }
//        for( int i = 0; i < DAMPING_COEFFICIENTS.length; i++ ) {
//            System.out.print( DAMPING_COEFFICIENTS[i]+", " );
//        }
//        System.out.println( "" );
//    }
//    static double damper = 0.999;
//    public static double[] DAMPING_COEFFICIENTS = new double[]{ damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper, damper};

    void damp( LightweightComplex[] _Psi ) {

        /*
        * Damps the values near the min and max positions
        * to prevent periodic boundary conditions.
        * Otherwise, the wave will appear to exit from one
        * edge of the display and enter on the other edge.
        */
        final int numberOfDampedSamples = SAMPLES_PER_DAMPING_COEFFICIENT * DAMPING_COEFFICIENTS.length;
        if( _Psi.length > numberOfDampedSamples ) {
            for( int i = 0; i < numberOfDampedSamples; i++ ) {
                final double scale = DAMPING_COEFFICIENTS[i / SAMPLES_PER_DAMPING_COEFFICIENT];
                // left edge...
                _Psi[i]._real *= scale;
                _Psi[i]._imaginary *= scale;
                // right edge...
                _Psi[_Psi.length - i - 1]._real *= scale;
                _Psi[_Psi.length - i - 1]._imaginary *= scale;
            }
        }
    }
}
