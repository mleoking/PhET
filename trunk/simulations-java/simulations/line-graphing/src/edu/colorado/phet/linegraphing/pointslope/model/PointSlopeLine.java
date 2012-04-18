// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.model;

import java.awt.Color;

import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptLine;

/**
 * Model of a line in point-slope form: y = m(x - x1) + y1
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeLine extends SlopeInterceptLine {

    private final double x1, y1;

    public PointSlopeLine( double x1, double y1, double rise, double run, Color color ) {
        this( x1, y1, rise, run, color, color );
    }

    public PointSlopeLine( double x1, double y1, double rise, double run, Color color, Color highlightColor ) {
        super( rise, run, solveY( 0, x1, y1, rise, run ), color, highlightColor );
        this.x1 = x1;
        this.y1 = y1;
    }

    // Duplicates a line with different colors
    public PointSlopeLine( PointSlopeLine line, Color color, Color highlightColor ) {
        this( line.x1, line.y1, line.rise, line.run, color, highlightColor );
    }

    // y = m(x - x1) + y1
    private static double solveY( double x, double x1, double y1, double rise, double run ) {
        assert( run != 0 );
        return ( ( rise / run ) * ( x - x1 ) ) + y1;
    }

    // x = (y - y1)/m + x1
    private static double solveX( double y, double x1, double y1, double rise, double run ) {
        assert( run != 0 && rise != 0 );
        return ( ( y - y1 ) / ( rise / run ) ) + x1;
    }
}
