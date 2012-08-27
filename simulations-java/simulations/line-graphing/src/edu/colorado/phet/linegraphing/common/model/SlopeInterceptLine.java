// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.Color;

/**
 * An immutable straight line, specified in slope-intercept form: y = mx + b.
 * Slope-intercept is a specialization of point-slope, with x1 = 0.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLine extends PointSlopeLine {
    public SlopeInterceptLine( double rise, double run, double yIntercept, Color color ) {
        super( 0, yIntercept, rise, run, color );
    }
}
