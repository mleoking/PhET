// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 11:45:18 AM
 */

public class PreFabSplines {
    public CubicSpline getBugDonut() {
        CubicSpline spline = new CubicSpline();
        spline.addControlPoint( 6, 2 );
        spline.addControlPoint( 5, 2 );
        spline.addControlPoint( 4, 1 );
        spline.addControlPoint( 12, 1 );
        spline.addControlPoint( 12, 3 );
        spline.addControlPoint( 10, 6 );
        spline.addControlPoint( 10, 4 );
        spline.addControlPoint( 7, 3 );
        spline.addControlPoint( 9, 3 );
        spline.addControlPoint( 6, 7 );
        spline.addControlPoint( 6, 3 );
        return spline;
    }

    public CubicSpline getTinyParabolic() {
        CubicSpline spline = new CubicSpline();
        spline.addControlPoint( 5 - 2, 5 );
        spline.addControlPoint( 7.5, 1 );
        spline.addControlPoint( 10 + 2, 5 );
        return spline;
    }

    public static class CubicSpline {
        private ArrayList pts = new ArrayList();

        public void addControlPoint( double x, double y ) {
            pts.add( new SerializablePoint2D( x, y ) );
        }

        public void addControlPoint( SerializablePoint2D point2D ) {
            addControlPoint( point2D.getX(), point2D.getY() );
        }

        public SerializablePoint2D[] getControlPoints() {
            return (SerializablePoint2D[])pts.toArray( new SerializablePoint2D[0] );
        }
    }

    public CubicSpline getParabolic() {
        CubicSpline spline = new CubicSpline();

        double dx = 1.5;
        double min = 2 + dx;
        double max = 11 + dx;
        double h = 7;
        spline.addControlPoint( min, h );
        spline.addControlPoint( ( max + min ) / 2.0, 1.5 );
        spline.addControlPoint( max, h );
        return spline;
    }

    public CubicSpline getLoop() {
        CubicSpline spline = new CubicSpline();
        spline.addControlPoint( 125, 198 );
        spline.addControlPoint( 250, 512 );
        spline.addControlPoint( 591, 447 );
        spline.addControlPoint( 543, 147 );
        spline.addControlPoint( 422, 333 );
        spline.addControlPoint( 810, 351 );
        spline.addControlPoint( 800, 198 );
        return spline;
    }

    public CubicSpline getTightParabolic() {
        CubicSpline spline = new CubicSpline();
        spline.addControlPoint( 118, 175 );
        spline.addControlPoint( 315, 569 );
        spline.addControlPoint( 554, 178 );
        return spline;
    }
}
