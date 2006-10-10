/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.energyskatepark.model.spline.AbstractSpline;
import edu.colorado.phet.energyskatepark.model.spline.CubicSpline;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 11:45:18 AM
 * Copyright (c) Sep 30, 2005 by Sam Reid
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

    public CubicSpline getParabolic() {
        CubicSpline spline = new CubicSpline();

        double min = 2;
        double max = 11;
        double h = 7;
        spline.addControlPoint( min, h );
        spline.addControlPoint( ( max + min ) / 2.0, 1.5 );
        spline.addControlPoint( max, h );
        return spline;
    }

    public static void init( AbstractSpline spline ) {

//        spline.addControlPoint( 150, 300 );
//        spline.addControlPoint( 200, 320 );
//        spline.addControlPoint( 350, 300 );
//        spline.addControlPoint( 400, 375 );

//        spline.addControlPoint( 125, 198 );
//        spline.addControlPoint( 250, 512 );
//        spline.addControlPoint( 591, 447 );
//        spline.addControlPoint( 419, 130 );

//        spline.addControlPoint( 125, 198 );
//        spline.addControlPoint( 250, 512 );
//        spline.addControlPoint( 591, 447 );
//        spline.addControlPoint( 747, 189 );
//
//        spline.addControlPoint( 125, 198 );
//        spline.addControlPoint( 250, 512 );
//        spline.addControlPoint( 591, 447 );
//        spline.addControlPoint( 620, 198 );
//        spline.addControlPoint( 700, 198 );
//        spline.addControlPoint( 750, 198 );
//        spline.addControlPoint( 800, 198 );

//        spline.addControlPoint( 125, 198 );
//        spline.addControlPoint( 250, 512 );
//        spline.addControlPoint( 591, 447 );
//        spline.addControlPoint( 543, 147 );
//        spline.addControlPoint( 422, 333 );
//        spline.addControlPoint( 810, 351 );
//        spline.addControlPoint( 800, 198 );
    }

//    private void addSpline() {
//        CubicSpline spline = new CubicSpline( NUM_CUBIC_SPLINE_SEGMENTS );
//        spline.addControlPoint( 50, 50 );
//        spline.addControlPoint( 150, 50 );
//        spline.addControlPoint( 300, 50 );
//        AbstractSpline rev = spline.createReverseSpline();
//
//        ec3Model.addSpline( spline, rev );
//        SplineGraphic splineGraphic = new SplineGraphic( this, spline, rev );
//        addSplineGraphic( splineGraphic );
//    }

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
