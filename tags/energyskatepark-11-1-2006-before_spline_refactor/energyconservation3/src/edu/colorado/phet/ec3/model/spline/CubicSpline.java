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
    private NatCubicSpline2D natcubicspline2d;

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

    protected void setAllDirty() {
        super.setAllDirty();
        this.natcubicspline2d = null;
    }

    public Point2D[] getInterpolationPoints() {
        return new NatCubic().interpolate( getControlPoints(), numSegments );
    }

    public Point2D evaluateAnalytical( double distAlongSpline ) {
        if( natcubicspline2d == null ) {
            natcubicspline2d = new NatCubicSpline2D( getControlPoints() );
        }
        return natcubicspline2d.evaluate( distAlongSpline );
    }

    public AbstractSpline createReverseSpline() {
        CubicSpline cubicSpline = new CubicSpline( numSegments );
        for( int i = 0; i < numControlPoints(); i++ ) {
            cubicSpline.addControlPoint( controlPointAt( numControlPoints() - 1 - i ) );
        }
        cubicSpline.setFrictionCoefficient(getFrictionCoefficient());
        return cubicSpline;
    }

    public AbstractVector2D getUnitNormalVector( double x ) {
        return getUnitParallelVector( x ).getNormalVector().getNormalizedInstance();
    }

    public double getLength() {
        return new NatCubicSpline2D( getControlPoints() ).getLength();
    }

    public AbstractVector2D getUnitParallelVector( double x ) {
        double dx = 1E-4;
        return new Vector2D.Double( evaluateAnalytical( Math.max( 0, x - dx ) ), evaluateAnalytical( Math.min( getLength(), x + dx ) ) ).getNormalizedInstance();
    }

}
