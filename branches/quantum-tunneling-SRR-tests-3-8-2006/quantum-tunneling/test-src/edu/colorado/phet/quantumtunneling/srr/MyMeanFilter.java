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

    public void filter( WavePacket wavePacket,int windowSize ) {
        if( array == null || array.length != wavePacket.getWaveFunctionValues().length ) {
            array = new LightweightComplex[wavePacket.getWaveFunctionValues().length];
            for( int i = 0; i < array.length; i++ ) {
                array[i] = new LightweightComplex();
            }
        }
//        int windowSize = 10;
        for( int i = windowSize; i < wavePacket.getWaveFunctionValues().length - windowSize; i++ ) {
            double realSum = 0;
            double imSum = 0;
            for( int j = -windowSize / 2; j < windowSize / 2; j++ ) {
                LightweightComplex lightweightComplex = wavePacket.getWaveFunctionValues()[i];
                realSum += lightweightComplex.getReal();
                imSum += lightweightComplex.getImaginary();
            }
            array[i].setValue( realSum/windowSize, imSum/windowSize );
//            if( sum < 0.01 ) {
//                array[i] = new LightweightComplex();
//            }
//            else {
//                LightweightComplex lightweightComplex = wavePacket.getWaveFunctionValues()[i];
//                array[i] = new LightweightComplex( lightweightComplex._real, lightweightComplex._imaginary );
//            }
        }
        for( int j = 0; j < array.length; j++ ) {
            wavePacket.getWaveFunctionValues()[j]._real = array[j].getReal();
            wavePacket.getWaveFunctionValues()[j]._imaginary = array[j].getImaginary();
        }
    }
}
