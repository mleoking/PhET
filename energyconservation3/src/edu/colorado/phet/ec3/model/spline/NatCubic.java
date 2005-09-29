package edu.colorado.phet.ec3.model.spline;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/*
http://www.cse.unsw.edu.au/~lambert/splines/NatCubic.java
*/

public class NatCubic {

    class PointArray {
        ArrayList points = new ArrayList();

        public PointArray() {
        }

        public PointArray( Point2D[] points ) {
            for( int i = 0; i < points.length; i++ ) {
                Point2D point = points[i];
                addPoint( point.getX(), point.getY() );
            }
        }

        public void addPoint( double x, double y ) {
            points.add( new Point2D.Double( x, y ) );
        }

        public int numPoints() {
            return points.size();
        }

        public double[] getXPoints() {
            double[] x = new double[numPoints()];
            for( int i = 0; i < points.size(); i++ ) {
                x[i] = getX( i );
            }
            return x;
        }

        public double[] getYPoints() {
            double[] y = new double[numPoints()];
            for( int i = 0; i < points.size(); i++ ) {
                y[i] = getY( i );
            }
            return y;
        }

        public double getX( int index ) {
            return pointAt( index ).getX();
        }

        private Point2D pointAt( int index ) {
            return (Point2D)points.get( index );
        }

        public double getY( int index ) {
            return pointAt( index ).getY();
        }
    }

    class Cubic {

        double a, b, c, d;         /* a + b*u + c*u^2 +d*u^3 */

        public Cubic( double a, double b, double c, double d ) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        public double eval( double u ) {
            return ( ( ( d * u ) + c ) * u + b ) * u + a;
        }
    }

    public Cubic[] calcNaturalCubic( int n, double[] x ) {
        double[] gamma = new double[n + 1];
        double[] delta = new double[n + 1];
        double[] D = new double[n + 1];
        int i;

        gamma[0] = 1.0f / 2.0f;
        for( i = 1; i < n; i++ ) {
            gamma[i] = 1 / ( 4 - gamma[i - 1] );
        }
        gamma[n] = 1 / ( 2 - gamma[n - 1] );

        delta[0] = 3 * ( x[1] - x[0] ) * gamma[0];
        for( i = 1; i < n; i++ ) {
            delta[i] = ( 3 * ( x[i + 1] - x[i - 1] ) - delta[i - 1] ) * gamma[i];
        }
        delta[n] = ( 3 * ( x[n] - x[n - 1] ) - delta[n - 1] ) * gamma[n];

        D[n] = delta[n];
        for( i = n - 1; i >= 0; i-- ) {
            D[i] = delta[i] - gamma[i] * D[i + 1];
        }

        /* now compute the coefficients of the cubics */
        Cubic[] C = new Cubic[n];
        for( i = 0; i < n; i++ ) {
            C[i] = new Cubic( x[i], D[i], 3 * ( x[i + 1] - x[i] ) - 2 * D[i] - D[i + 1],
                              2 * ( x[i] - x[i + 1] ) + D[i] + D[i + 1] );
        }
        return C;
    }

    public Point2D evaluate( Point2D[] points, double distAlongSpline ) {
        PointArray pts = new PointArray( points );
        Cubic[] X = calcNaturalCubic( pts.numPoints() - 1, pts.getXPoints() );
        Cubic[] Y = calcNaturalCubic( pts.numPoints() - 1, pts.getYPoints() );

        Point2D.Double pt = new Point2D.Double( X[0].eval( distAlongSpline ), Y[0].eval( distAlongSpline ) );
        return pt;
    }

    public Point2D[] interpolate( Point2D[] points, int segments ) {
        if( points.length < 2 ) {
            Point2D[] out = new Point2D[points.length];
            for( int i = 0; i < out.length; i++ ) {
                out[i] = new Point2D.Double( points[i].getX(), points[i].getY() );
            }
            return out;
        }
        else {
            PointArray pts = new PointArray( points );
            PointArray out = new PointArray();
            Cubic[] X = calcNaturalCubic( pts.numPoints() - 1, pts.getXPoints() );
            Cubic[] Y = calcNaturalCubic( pts.numPoints() - 1, pts.getYPoints() );

            /* very crude technique - just break each segment up into segments lines */
            out.addPoint( X[0].eval( 0 ), Y[0].eval( 0 ) );
            for( int i = 0; i < X.length; i++ ) {
                for( int j = 1; j <= segments; j++ ) {
                    float u = j / (float)segments;
                    out.addPoint( ( X[i].eval( u ) ), ( Y[i].eval( u ) ) );
                }
            }
            ArrayList mypath = new ArrayList();
            mypath.add( new Point2D.Double( out.getX( 0 ), out.getY( 0 ) ) );
            for( int i = 1; i < out.numPoints(); i++ ) {
                mypath.add( new Point2D.Double( out.getX( i ), out.getY( i ) ) );
            }

            return (Point2D[])mypath.toArray( new Point2D.Double[0] );
        }
    }

}
