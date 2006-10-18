/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model.spline;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.EC3Canvas;

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
        return new NatCubic().interpolate( getControlPoints(), numSegments );
    }

    public Point2D evaluateAnalytical( double distAlongSpline ) {
        return new NatCubic().evaluate( getControlPoints(), distAlongSpline );
    }

    public AbstractSpline createReverseSpline() {
        CubicSpline cubicSpline = new CubicSpline( numSegments );
        for( int i = 0; i < numControlPoints(); i++ ) {
            cubicSpline.addControlPoint( controlPointAt( numControlPoints() - 1 - i ) );
        }
        return cubicSpline;
    }

    public AbstractVector2D getUnitNormalVector( double x ) {
        //todo ensure values are in bounds
        double dx = 1E-4;
        Vector2D.Double parallelEstimate = new Vector2D.Double( x - dx, x + dx );
        return parallelEstimate.getNormalVector().getNormalizedInstance();
    }

    public double getLength() {
        return new NatCubicSpline2D( getControlPoints() ).getLength();
    }

}
