/**
 * Class: MedianFilter
 * Package: edu.colorado.phet.common.math
 * Author: Another Guy
 * Date: Nov 24, 2003
 * asdfasdfasdfasdf
 */
package edu.colorado.phet.common_cck.math;

import java.util.Arrays;

public class MedianFilter {

    private double[] source;
    private double[] target;

    public MedianFilter( double[] data ) {
        source = data;
        target = new double[source.length];
    }

    public static double getMedian( double[] data ) {
        double[] workingData = new double[data.length];
        for( int i = 0; i < data.length; i++ ) {
            workingData[i] = data[i];
        }
        return getMedianInternal( workingData );
    }

    private static double getMedianInternal( double[] data ) {
        Arrays.sort( data );
        return data[data.length / 2];
    }

    public double[] filter( int width ) {
        double[] temp = new double[width];

        for( int i = 0; i < source.length - width + 1; i++ ) {

            // sort local group
            for( int k = 0; k < width; k++ ) {
                temp[k] = source[i + k];
            }
            target[i + width / 2] = getMedianInternal( temp );
        }

        // Fix up the ends of the target array
        for( int i = 0; i < width / 2; i++ ) {
            target[i] = target[width / 2];
        }
        for( int i = target.length - width / 2; i < target.length; i++ ) {
            target[i] = target[target.length - width / 2 - 1];
        }
        for( int i = 0; i < source.length; i++ ) {
            source[i] = target[i];
        }

        return source;
    }

    public static void main( String[] args ) {
        double[] d = new double[]{9, 8, 7, 6, 5, 6, 7, 4, 5, 8, 9};
        MedianFilter mf = new MedianFilter( d );
        d = mf.filter( 3 );
        for( int i = 0; i < d.length; i++ ) {
            System.out.println( d[i] );
        }
    }
}
