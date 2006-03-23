/* Copyright 2004, Sam Reid */
package edu.colorado.phet.quantumtunneling.srr;

import edu.colorado.phet.quantumtunneling.util.LightweightComplex;

/**
 * User: Sam Reid
 * Date: Mar 10, 2006
 * Time: 10:30:58 PM
 * Copyright (c) Mar 10, 2006 by Sam Reid
 */

public class MyMeanFilter {

    private LightweightComplex[] array;

    public void filter( LightweightComplex[]psi, int windowHalfWidth ) {
//        LightweightComplex[]out = new LightweightComplex[psi.length];
        if( array == null || array.length != psi.length ) {
            array = new LightweightComplex[psi.length];
            for( int i = 0; i < array.length; i++ ) {
                array[i] = new LightweightComplex();
            }
        }
//        int windowSize = 10;
        for( int i = windowHalfWidth; i < psi.length - windowHalfWidth; i++ ) {
            double realSum = 0;
            double imSum = 0;
            for( int j = -windowHalfWidth; j <= windowHalfWidth; j++ ) {
                LightweightComplex lightweightComplex = psi[i + j];
                realSum += lightweightComplex.getReal();
                imSum += lightweightComplex.getImaginary();
            }
            array[i].setValue( realSum / ( windowHalfWidth * 2 + 1 ), imSum / ( windowHalfWidth * 2 + 1 ) );
//            if( sum < 0.01 ) {
//                array[i] = new LightweightComplex();
//            }
//            else {
//                LightweightComplex lightweightComplex = psi[i];
//                array[i] = new LightweightComplex( lightweightComplex._real, lightweightComplex._imaginary );
//            }
        }
        for( int i = windowHalfWidth; i < psi.length - windowHalfWidth; i++ ) {
            psi[i]._real = array[i].getReal();
            psi[i]._imaginary = array[i].getImaginary();
        }
    }

    public static void main( String[] args ) {
        LightweightComplex[]test = new LightweightComplex[]{
                new LightweightComplex( 0, 0 ),
                new LightweightComplex( 5, 0 ),
                new LightweightComplex( 9, 0 ),
                new LightweightComplex( 5, 0 ),
                new LightweightComplex( 0, 0 ),
                new LightweightComplex( 0, 0 ),
                new LightweightComplex( 3, 0 ),
                new LightweightComplex( 3, 0 ),
                new LightweightComplex( 3, 0 ),
                new LightweightComplex( 3, 0 ),
                new LightweightComplex( 3, 0 ),
                new LightweightComplex( 3, 0 ),
                new LightweightComplex( 3, 0 )
        };
        new MyMeanFilter().filter( test, 1 );
        for( int i = 0; i < test.length; i++ ) {
//            LightweightComplex lightweightComplex = test[i];
            System.out.println( "lightweightComplex[i] = " + test[i] );
        }
    }
}
