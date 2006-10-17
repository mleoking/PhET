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

    public NatCubicSpline2D( Point2D[] controlPoints ) {
        NatCubic.PointArray pts = new NatCubic.PointArray( controlPoints );
        this.X = NatCubic.calcNaturalCubic( pts.getXPoints() );
        this.Y = NatCubic.calcNaturalCubic( pts.getYPoints() );
    }

    public NatCubicSpline2D( NatCubic.Cubic[] x, NatCubic.Cubic[] y ) {
        X = x;
        Y = y;
    }

    public double getLength() {
        return X.length;
    }

    public Point2D evaluate( double x ) {
        int i = (int)Math.floor( x );
        double distAlongCubic = x - i;
//        System.out.println( "x=" + x + ", i=" + i + ", dist=" + distAlongCubic + ", X.length=" + X.length );
        if( i == X.length && Math.abs( distAlongCubic ) < 1E-6 ) {
            i = X.length - 1;
            distAlongCubic = 0;
        }
        return new Point2D.Double( X[i].eval( distAlongCubic ), Y[i].eval( distAlongCubic ) );
    }
}
