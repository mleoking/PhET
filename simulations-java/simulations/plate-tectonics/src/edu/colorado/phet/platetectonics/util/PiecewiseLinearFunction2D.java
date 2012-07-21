// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.util;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * An open-ended piecewise linear function for 2D results
 */
public class PiecewiseLinearFunction2D implements Function1<Double, Double> {
    private List<Vector2D> points = new ArrayList<Vector2D>();

    public PiecewiseLinearFunction2D( Vector2D... points ) {
        for ( Vector2D point : points ) {
            this.points.add( point );
        }
    }

    public Double apply( Double value ) {
        // naive method of doing this
        for ( int i = 0; i < points.size() - 1; i++ ) {
            if ( points.get( i + 1 ).getX() > value ) {
                return evaluateAtSegment( i, value );
            }
        }

        // all points too small, evaluate the last
        return evaluateAtSegment( points.size() - 2, value );
    }

    private double evaluateAtSegment( int segmentIndex, double value ) {
        Vector2D left = points.get( segmentIndex );
        Vector2D right = points.get( segmentIndex + 1 );
        double deltaX = right.getX() - left.getX();
        double deltaY = right.getY() - left.getY();

        double ratio = ( value - left.getX() ) / deltaX;
        return left.getY() + ratio * deltaY;
    }
}
