package edu.colorado.phet.ec3.test.phys1d;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 14, 2007
 * Time: 3:09:47 AM
 * Copyright (c) Feb 14, 2007 by Sam Reid
 */

public class CubicSpline2D {
    private CubicSpline x;
    private CubicSpline y;

    public CubicSpline2D( CubicSpline x, CubicSpline y ) {
        this.x = x;
        this.y = y;
    }

    public static CubicSpline2D interpolate( Point2D[] pts ) {
        double[] s = new double[pts.length];
        double[] x = new double[pts.length];
        double[] y = new double[pts.length];
        for( int i = 0; i < pts.length; i++ ) {
            s[i] = ( 1.0 / ( pts.length - 1 ) ) * i;//*2.0;
            x[i] = pts[i].getX();
            y[i] = pts[i].getY();
        }
        s[s.length - 1] = Math.round( s[s.length - 1] );//to be exact, in case of roundoff error
        return new CubicSpline2D( CubicSpline.interpolate( s, x ), CubicSpline.interpolate( s, y ) );
    }

    public Point2D evaluate( double s ) {
        return new Point2D.Double( x.evaluate( s ), y.evaluate( s ) );
    }

    public static void main( String[] args ) {

        CubicSpline2D cubicSpline2D = CubicSpline2D.interpolate( new Point2D[]{
                new Point2D.Double( 0, 0 ),
                new Point2D.Double( 1, 1 ),
                new Point2D.Double( 2, 0 )
        } );
        for( double s = 0.0; s < 1.0; s += 0.1 ) {
            Point2D at = cubicSpline2D.evaluate( s );
//            System.out.println( "s = " + s + ", at=" + at );
//            System.out.println( at.getX()+"\t"+at.getY() );
            System.out.println( at.getY() );
        }
    }
}
