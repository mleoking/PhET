/* Copyright 2004, Sam Reid */
package edu.colorado.phet.quantumtunneling.srr;

import edu.colorado.phet.quantumtunneling.model.WavePacket;
import edu.colorado.phet.quantumtunneling.util.LightweightComplex;

/**
 * User: Sam Reid
 * Date: Mar 10, 2006
 * Time: 10:30:58 PM
 * Copyright (c) Mar 10, 2006 by Sam Reid
 */

public class MyMeanFilter {

    private LightweightComplex[] array;

    public void filter( LightweightComplex[]psi,int windowSize ) {
        if( array == null || array.length != psi.length ) {
            array = new LightweightComplex[psi.length];
            for( int i = 0; i < array.length; i++ ) {
                array[i] = new LightweightComplex();
            }
        }
//        int windowSize = 10;
        for( int i = windowSize; i < psi.length - windowSize; i++ ) {
            double realSum = 0;
            double imSum = 0;
            for( int j = -windowSize / 2; j < windowSize / 2; j++ ) {
                LightweightComplex lightweightComplex = psi[i];
                realSum += lightweightComplex.getReal();
                imSum += lightweightComplex.getImaginary();
            }
            array[i].setValue( realSum/windowSize, imSum/windowSize );
//            if( sum < 0.01 ) {
//                array[i] = new LightweightComplex();
//            }
//            else {
//                LightweightComplex lightweightComplex = psi[i];
//                array[i] = new LightweightComplex( lightweightComplex._real, lightweightComplex._imaginary );
//            }
        }
        for( int j = 0; j < array.length; j++ ) {
            psi[j]._real = array[j].getReal();
            psi[j]._imaginary = array[j].getImaginary();
        }
    }
}
