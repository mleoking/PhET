/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.model.spline;

import edu.colorado.phet.energyskatepark.EC3Canvas;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:03:37 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class CubicSpline extends AbstractSpline {
    private int numSegments;

    public CubicSpline() {
        this( EC3Canvas.NUM_CUBIC_SPLINE_SEGMENTS );
    }

    public CubicSpline( int numSegments ) {
        this.numSegments = numSegments;
    }

    public AbstractSpline copySpline() {
        CubicSpline cubicSpline = (CubicSpline)super.clone();
        cubicSpline.numSegments = this.numSegments;
        return cubicSpline;
    }

    public Point2D[] getInterpolationPoints() {
        NatCubic natCubic = new NatCubic();
        return natCubic.interpolate( getControlPoints(), numSegments );
    }

    public AbstractSpline createReverseSpline() {
        CubicSpline cubicSpline = new CubicSpline( numSegments );
        for( int i = 0; i < numControlPoints(); i++ ) {
            cubicSpline.addControlPoint( controlPointAt( numControlPoints() - 1 - i ) );
        }
        return cubicSpline;
    }

}
