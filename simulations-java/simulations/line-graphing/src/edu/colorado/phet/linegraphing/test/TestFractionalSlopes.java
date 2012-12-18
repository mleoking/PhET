// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.test;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.linegraphing.common.model.Fraction;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Iterates over all possible placements of 2 points on a symmetric 20x20 graph,
 * and displays the y-intercept as both a decimal and improper fraction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestFractionalSlopes {

    public static void main( String[] args ) {

        System.out.println( "x1 | y1 | x2 | y2 | rise | run | b" );
        int numberOfFractions = 0;
        for ( int x1 = -10; x1 <= 10; x1++ ) {
            for ( int y1 = -10; y1 <= 10; y1++ ) {
                for ( int x2 = -10; x2 <= 10; x2++ ) {
                    for ( int y2 = -10; y2 <= 10; y2++ ) {
                        if ( x1 == x2 && y1 == y2 ) {
                            // 2 points are the same, skip
                        }
                        else if ( x1 == x2 ) {
                            // slope is undefined, skip
                        }
                        else {
                            Line line = new Line( x1, y1, x2, y2, Color.BLACK );
                            double b = line.solveY( 0 );
                            if ( !MathUtil.isInteger( b ) ) {
                                Fraction yIntercept = line.getYIntercept();
                                System.out.println( x1 + " " + y1 + " " + x2 + " " + y2 + " " + b + " " + yIntercept.numerator + " " + yIntercept.denominator );
                            }
                        }
                    }
                }
            }
        }
        System.out.println( "numberOfFractions=" + numberOfFractions );
    }
}
