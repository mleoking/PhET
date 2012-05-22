// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.model;

import java.awt.Color;
import java.text.MessageFormat;

import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.StraightLine;

/**
 * Model of an immutable line, using slope-intercept form, y = mx + b.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLine extends StraightLine {

    public static final SlopeInterceptLine Y_EQUALS_X_LINE = new SlopeInterceptLine( 1, 1, 0, LGColors.Y_EQUALS_X );  // y = x
    public static final SlopeInterceptLine Y_EQUALS_NEGATIVE_X_LINE = new SlopeInterceptLine( -1, 1, 0, LGColors.Y_EQUALS_NEGATIVE_X ); // y = -x

    public final double intercept;

    public SlopeInterceptLine( double rise, double run, double intercept, Color color ) {
        this( rise, run, intercept, color, color );
    }

    public SlopeInterceptLine( double rise, double run, double intercept, Color color, Color highlightColor ) {
        super( rise, run, color, highlightColor );
        this.intercept = intercept;
    }

    // Duplicates a line with different colors
    public SlopeInterceptLine( SlopeInterceptLine line, Color color, Color highlightColor ) {
        this( line.rise, line.run, line.intercept, color, highlightColor );
    }

    // y = mx + b
    public double solveY( double x ) {
        assert( run != 0 );
        return ( ( rise / run ) * x ) + intercept;
    }

    // x = (y-b)/m
    public double solveX( double y ) {
        assert ( rise != 0 && run != 0 );
        return ( y - intercept ) / ( rise / run );
    }

    @Override public String toString() {
        return MessageFormat.format( "y=({0}/{1})x+{2}", rise, run, intercept );
    }
}
