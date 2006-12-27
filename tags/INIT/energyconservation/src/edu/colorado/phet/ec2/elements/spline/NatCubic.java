package edu.colorado.phet.ec2.elements.spline;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class NatCubic {
    protected FloatPolygon pts = new FloatPolygon();
    private Cubic[] X;
    private Cubic[] Y;
    FloatPolygon polygon;
    private GeneralPath path;
    SegmentPath segmentPath;

    public GeneralPath getPath() {
        return path;
    }
    /* calculates the natural cubic spline that interpolates
y[0], y[1], ... y[n]
The first segment is returned as
C[0].a + C[0].b*u + C[0].c*u^2 + C[0].d*u^3 0<=u <1
the other segments are in C[1], C[2], ...  C[n-1] */

    public Cubic[] calcNaturalCubic( int n, float[] x ) {
        float[] gamma = new float[n + 1];
        float[] delta = new float[n + 1];
        float[] D = new float[n + 1];
        int i;
        /* We solve the equation
           [2 1       ] [D[0]]   [3(x[1] - x[0])  ]
           |1 4 1     | |D[1]|   |3(x[2] - x[0])  |
           |  1 4 1   | | .  | = |      .         |
           |    ..... | | .  |   |      .         |
           |     1 4 1| | .  |   |3(x[n] - x[n-2])|
           [       1 2] [D[n]]   [3(x[n] - x[n-1])]

           by using row operations to convert the matrix to upper triangular
           and then back sustitution.  The D[i] are the derivatives at the knots.
           */

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

    public void paint( Graphics2D g ) {
        g.draw( path );
    }

    public void addPoint( float x, float y ) {
        pts.addPoint( x, y );
    }

    /* draw a cubic spline */
    public void computePaintState( int steps ) {
        FloatPolygon mypolygon = new FloatPolygon();
        if( pts.numPoints() >= 2 ) {
            X = calcNaturalCubic( pts.numPoints() - 1, pts.getXPoints() );
            Y = calcNaturalCubic( pts.numPoints() - 1, pts.getYPoints() );

            /* very crude technique - just break each segment up into steps lines */
            mypolygon.addPoint( X[0].eval( 0 ), Y[0].eval( 0 ) );
            for( int i = 0; i < X.length; i++ ) {
                for( int j = 1; j <= steps; j++ ) {
                    float u = j / (float)steps;
                    mypolygon.addPoint( ( X[i].eval( u ) ), ( Y[i].eval( u ) ) );
                }
            }
            GeneralPath mypath = new GeneralPath();
            segmentPath = new SegmentPath();
            mypath.moveTo( mypolygon.getX( 0 ), mypolygon.getY( 0 ) );
            segmentPath.startAt( mypolygon.getX( 0 ), mypolygon.getY( 0 ) );
            for( int i = 1; i < mypolygon.numPoints(); i++ ) {
                mypath.lineTo( mypolygon.getX( i ), mypolygon.getY( i ) );
                segmentPath.lineTo( mypolygon.getX( i ), mypolygon.getY( i ) );
            }
            this.path = mypath;
        }
    }

    public SegmentPath getSegmentPath() {
        return segmentPath;
    }

    public SegmentPath[] listSegmentPaths() {
        return new SegmentPath[0];
    }
}
