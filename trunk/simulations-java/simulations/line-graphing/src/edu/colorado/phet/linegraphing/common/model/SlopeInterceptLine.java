// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.Color;

import edu.colorado.phet.linegraphing.common.LGColors;

/**
 * An immutable line, specified in slope-intercept form: y = mx + b.
 * Slope-intercept is a specialization of point-slope, with x1 = 0.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLine extends PointSlopeLine {

    // standard lines
    public static final SlopeInterceptLine Y_EQUALS_X_LINE = new SlopeInterceptLine( 1, 1, 0, LGColors.Y_EQUALS_X );  // y = x
    public static final SlopeInterceptLine Y_EQUALS_NEGATIVE_X_LINE = new SlopeInterceptLine( -1, 1, 0, LGColors.Y_EQUALS_NEGATIVE_X ); // y = -x

    public SlopeInterceptLine( double rise, double run, double yIntercept, Color color ) {
        super( 0, yIntercept, rise, run, color );
    }
}
