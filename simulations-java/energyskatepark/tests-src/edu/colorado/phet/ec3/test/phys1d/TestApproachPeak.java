package edu.colorado.phet.ec3.test.phys1d;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 1, 2007
 * Time: 11:37:45 PM
 * Copyright (c) Mar 1, 2007 by Sam Reid
 */

public class TestApproachPeak extends TestPhysics1D {

    public TestApproachPeak() {
        getCubicSpline().setControlPoints( new Point2D[]{
                new Point2D.Double( 0, 0 ),
                new Point2D.Double( 1, 2 ),
                new Point2D.Double( 2, 1 ),
                new Point2D.Double( 3, 2 ),
                new Point2D.Double( 4, 0 )
        } );
        getParticle().switchToTrack( getCubicSpline(), 0.91075, true );
    }

    public static void main( String[] args ) {
        new TestApproachPeak().start();
    }
}
