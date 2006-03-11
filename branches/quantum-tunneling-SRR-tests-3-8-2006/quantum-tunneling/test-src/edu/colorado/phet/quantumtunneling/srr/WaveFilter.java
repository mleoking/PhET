/* Copyright 2004, Sam Reid */
package edu.colorado.phet.quantumtunneling.srr;

import edu.colorado.phet.quantumtunneling.model.WavePacket;
import edu.colorado.phet.quantumtunneling.util.LightweightComplex;

/**
 * User: Sam Reid
 * Date: Mar 10, 2006
 * Time: 10:21:33 PM
 * Copyright (c) Mar 10, 2006 by Sam Reid
 */

public class WaveFilter {
    private LightweightComplex[] array;

    public void filter( WavePacket wavePacket ) {
        if( array == null || array.length != wavePacket.getWaveFunctionValues().length ) {
            array = new LightweightComplex[wavePacket.getWaveFunctionValues().length];
        }
        int windowSize = 10;
        for( int i = windowSize; i < wavePacket.getWaveFunctionValues().length - windowSize; i++ ) {
            double sum = 0;
            for( int j = -windowSize / 2; j < windowSize / 2; j++ ) {
                LightweightComplex lightweightComplex = wavePacket.getWaveFunctionValues()[i];
                sum += lightweightComplex.getAbs();
            }
            if( sum < 0.01 ) {
                array[i] = new LightweightComplex();
            }
            else {
                LightweightComplex lightweightComplex = wavePacket.getWaveFunctionValues()[i];
                array[i] = new LightweightComplex( lightweightComplex._real, lightweightComplex._imaginary );
            }
        }
        for( int j = 0; j < array.length; j++ ) {
            LightweightComplex lightweightComplex = array[j];
            if( lightweightComplex != null ) {
                wavePacket.getWaveFunctionValues()[j]._real = lightweightComplex.getReal();
                wavePacket.getWaveFunctionValues()[j]._imaginary = lightweightComplex.getImaginary();
            }
        }
    }
}
