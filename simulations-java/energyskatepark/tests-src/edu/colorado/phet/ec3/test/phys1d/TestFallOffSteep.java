package edu.colorado.phet.ec3.test.phys1d;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 1, 2007
 * Time: 11:37:45 PM
 * Copyright (c) Mar 1, 2007 by Sam Reid
 */

public class TestFallOffSteep extends TestPhysics1D {

    public TestFallOffSteep() {
        getCubicSpline().setControlPoints( new Point2D.Double[]{new Point2D.Double( 1.0, 0.5 ), new Point2D.Double( 2.0, 1.0 ), new Point2D.Double( 2.7499999999999996, 4.519999999999998 ), new Point2D.Double( 4.699999999999995, 4.219999999999991 ), new Point2D.Double( 4.070000000000002, 0.12999999999999984 )} );
        getParticle().switchToTrack( getCubicSpline(), 0.92, true );
    }

    public static void main( String[] args ) {
        new TestFallOffSteep().start();
    }
}
