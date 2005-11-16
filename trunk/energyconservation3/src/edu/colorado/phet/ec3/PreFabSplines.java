/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.CubicSpline;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 11:45:18 AM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class PreFabSplines {
    public CubicSpline getTinyParabolic() {
        CubicSpline spline = new CubicSpline();
        spline.addControlPoint( 5 - 2, 5 );
        spline.addControlPoint( 7.5, 1 );
        spline.addControlPoint( 10 + 2, 5 );
        return spline;
    }

    public CubicSpline getParabolic() {
        CubicSpline spline = new CubicSpline();

        spline.addControlPoint( 47, 170 );
        spline.addControlPoint( 336, 543 );
        spline.addControlPoint( 669, 152 );
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
