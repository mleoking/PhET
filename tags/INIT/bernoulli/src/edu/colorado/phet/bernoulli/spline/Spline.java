package edu.colorado.phet.bernoulli.spline;

import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 2:35:12 PM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public class Spline extends SimpleObservable {
    ArrayList interpolatedPoints = new ArrayList();
    ArrayList controlPoints = new ArrayList();
    private int steps;

    public Spline( int steps ) {
        this.steps = steps;
    }

    public Spline( Point2D.Double[] controlPoints, int steps ) {
        this( steps );
        addControlPoints( controlPoints );
    }

    private void addControlPoints( Point2D.Double[] controlPoints ) {
        this.controlPoints.addAll( Arrays.asList( controlPoints ) );
        this.recomputeState();
    }

    /* calculates the natural cubic spline that interpolates
y[0], y[1], ... y[n]
The first segment is returned as
C[0].a + C[0].b*u + C[0].c*u^2 + C[0].d*u^3 0<=u <1
the other segments are in C[1], C[2], ...  C[n-1] */

    public static Cubic[] calcNaturalCubic( float[] x ) {
        int n = x.length - 1;
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

    public int numInterpolatedPoints() {
        return interpolatedPoints.size();
    }

    public Point2D.Double interpolatedPointAt( int i ) {
        return (Point2D.Double)interpolatedPoints.get( i );
    }

    public void addControlPoint( double x, double y ) {
        controlPoints.add( new Point2D.Double( x, y ) );
        recomputeState();
    }

    public float[] getXControlPoints() {
        float[] out = new float[controlPoints.size()];
        for( int i = 0; i < controlPoints.size(); i++ ) {
            Point2D.Double aDouble = (Point2D.Double)controlPoints.get( i );
            out[i] = (float)aDouble.x;
        }
        return out;
    }

    public float[] getYControlPoints() {
        float[] out = new float[controlPoints.size()];
        for( int i = 0; i < controlPoints.size(); i++ ) {
            Point2D.Double aDouble = (Point2D.Double)controlPoints.get( i );
            out[i] = (float)aDouble.y;
        }
        return out;
    }

    public int numControlPoints() {
        return controlPoints.size();
    }

    public void recomputeState() {
        interpolatedPoints.clear();
        if( controlPoints.size() >= 2 ) {
            Cubic[] X = calcNaturalCubic( getXControlPoints() );
            Cubic[] Y = calcNaturalCubic( getYControlPoints() );

            /* very crude technique - just break each segment up into steps lines */
            interpolatedPoints.add( new Point2D.Double( X[0].eval( 0 ), Y[0].eval( 0 ) ) );
            for( int i = 0; i < X.length; i++ ) {
                for( int j = 1; j <= steps; j++ ) {
                    float u = j / ( (float)steps );
                    double xcoord = X[i].eval( u );
                    double ycoord = Y[i].eval( u );
                    interpolatedPoints.add( new Point2D.Double( xcoord, ycoord ) );
                }
            }
        }
        updateObservers();
    }

    public void translateControlPoint( int index, Point2D.Double modelDX ) {
        Point2D.Double controlPoint = controlPointAt( index );
        controlPoint.x += modelDX.x;
        controlPoint.y += modelDX.y;
        recomputeState();
    }

    public Point2D.Double controlPointAt( int index ) {
        return (Point2D.Double)controlPoints.get( index );
    }

    public void setControlPoints( Point2D.Double[] pts ) {
        controlPoints.clear();
        controlPoints.addAll( Arrays.asList( pts ) );
        recomputeState();
    }

}
