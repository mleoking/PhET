/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.operators.ProbabilityValue;

import java.util.Arrays;


/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 1:46:56 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class Wavefunction {
    public static void setNorm( Complex[][] wavefunction, double newScale ) {
        double totalProbability = new ProbabilityValue().compute( wavefunction );
        double scale = 1.0 / Math.sqrt( totalProbability );
        scale( wavefunction, scale * newScale );
    }

    public static void normalize( Complex[][] wavefunction ) {
        double totalProbability = new ProbabilityValue().compute( wavefunction );
//        System.out.println( "totalProbability = " + totalProbability );
        double scale = 1.0 / Math.sqrt( totalProbability );
        scale( wavefunction, scale );
        double postProb = new ProbabilityValue().compute( wavefunction );
//        System.out.println( "postProb = " + postProb );

        double diff = 1.0 - postProb;
        if( !( Math.abs( diff ) < 0.0001 ) ) {
            System.out.println( "Error in probability normalization." );
//            throw new RuntimeException( "Error in probability normalization." );
        }
    }

    public static void scale( Complex[][] wavefunction, double scale ) {

        for( int i = 0; i < wavefunction.length; i++ ) {
            for( int j = 0; j < wavefunction[i].length; j++ ) {
                Complex complex = wavefunction[i][j];
                complex.scale( scale );
            }
        }
    }

    public static boolean containsLocation( Complex[][] wavefunction, int i, int k ) {
        return i >= 0 && i < wavefunction.length && k >= 0 && k < wavefunction[0].length;
    }

    public static String toString( Complex[] complexes ) {
        return Arrays.asList( complexes ).toString();
    }

    public static Complex[][] newInstance( int w, int h ) {
        Complex[][] copy = new Complex[w][h];
        for( int i = 0; i < copy.length; i++ ) {
            for( int j = 0; j < copy[i].length; j++ ) {
                copy[i][j] = new Complex();
            }
        }
        return copy;
    }

    public static Complex[][] copy( Complex[][] w ) {
        Complex[][] copy = new Complex[w.length][w[0].length];
        for( int i = 0; i < copy.length; i++ ) {
            for( int j = 0; j < copy[i].length; j++ ) {
                copy[i][j] = new Complex( w[i][j] );
            }
        }
        return copy;
    }

    public static void copy( Complex[][] src, Complex[][] dst ) {
        for( int i = 0; i < src.length; i++ ) {
            for( int j = 0; j < src[i].length; j++ ) {
                dst[i][j].setValue( src[i][j] );
            }
        }
    }
}
