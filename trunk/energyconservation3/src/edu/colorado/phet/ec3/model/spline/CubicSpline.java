/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model.spline;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:03:37 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class CubicSpline extends AbstractSpline {
    int numSegments;

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
        super.setReverseSpline( cubicSpline );
        cubicSpline.setReverseSpline( this );
        return cubicSpline;
    }

}
