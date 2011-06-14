// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.energyskatepark.util.EnergySkateParkLogging;

/**
 * User: Sam Reid
 * Date: Feb 17, 2007
 * Time: 8:51:44 PM
 */

public class BinarySearch {
    interface Function {
        double evaluate( double x );
    }

    private static double search( Function f, double lowerBound, double upperBound, double targetValue, double tolerance, int maxIterations ) {
        double guess = ( upperBound + lowerBound ) / 2;
        double value = f.evaluate( guess );
        int count = 0;
        while( Math.abs( value - targetValue ) > tolerance ) {
            if( value > targetValue ) {
                upperBound = guess;
            }
            else if( value < targetValue ) {
                lowerBound = guess;
            }
            guess = ( upperBound + lowerBound ) / 2;
            value = f.evaluate( guess );
            count++;
            if( count > maxIterations ) {
                throw new RuntimeException( "Search failed after " + count + " iterations" );
            }
        }
        return guess;
    }

    public static void main( String[] args ) {
        Function f = new Function() {
            public double evaluate( double x ) {
                return x * x;
            }
        };
        double binarySearch = BinarySearch.search( f, 0, 3.5, 1, 0.01, 100 );
        EnergySkateParkLogging.println( "binarySearch = " + binarySearch );
    }
}
