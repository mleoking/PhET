package edu.colorado.phet.ec3.model.spline;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Oct 17, 2006
 * Time: 4:41:13 PM
 * Copyright (c) Oct 17, 2006 by Sam Reid
 */

public class NatCubicSpline2D implements Spline2D {
    private NatCubic.Cubic[] X;
    private NatCubic.Cubic[] Y;
    private Point2D[] controlPoints;

    public NatCubicSpline2D( Point2D[] controlPoints ) {
        this.controlPoints = controlPoints;
        NatCubic.PointArray pts = new NatCubic.PointArray( controlPoints );
        this.X = NatCubic.calcNaturalCubic( pts.getXPoints() );
        this.Y = NatCubic.calcNaturalCubic( pts.getYPoints() );
    }

    public Point2D[] getControlPoints() {
        return controlPoints;
    }

    public double getLength() {
        return X.length;
    }

    public Point2D evaluate( double x ) {
        int segmentIndex = (int)Math.floor( x );
        double distAlongSegment = x - segmentIndex;
//        System.out.println( "x=" + x + ", i=" + i + ", dist=" + distAlongCubic + ", X.length=" + X.length );
        //this hack covers the case in which we accidentally stepped slightly past the end of the spline.
        if( segmentIndex == X.length && Math.abs( distAlongSegment ) < 1E-6 ) {
            segmentIndex = X.length - 1;
            distAlongSegment = 1;
        }
        if( segmentIndex < 0 ) {
            segmentIndex = 0;
        }
        if( segmentIndex >= X.length ) {
            segmentIndex = X.length - 1;
        }

        return new Point2D.Double( X[segmentIndex].eval( distAlongSegment ), Y[segmentIndex].eval( distAlongSegment ) );
    }
}
