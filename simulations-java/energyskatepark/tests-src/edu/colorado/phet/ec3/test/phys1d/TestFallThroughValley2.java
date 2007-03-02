package edu.colorado.phet.ec3.test.phys1d;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 1, 2007
 * Time: 11:37:45 PM
 * Copyright (c) Mar 1, 2007 by Sam Reid
 */

public class TestFallThroughValley2 extends TestPhysics1D {

    public TestFallThroughValley2() {
        double w = 1.0;
        getCubicSpline().setControlPoints( new Point2D[]{
                new Point2D.Double( 1 - w / 2, 0 ),
                new Point2D.Double( 1, 3 ),
                new Point2D.Double( 1 + w / 2, 0 ),
//                new Point2D.Double( 3, 2 ),
//                new Point2D.Double( 4, 0 )
        } );
//        getParticle().switchToTrack( getCubicSpline(), 0.92, true );
        getParticle().setFreeFall();
        getParticle().setPosition( 1.2, 0.5 );
        getParticle().setVelocity( 8, 0 );
    }

    public static void main( String[] args ) {
        new TestFallThroughValley2().start();
    }
}
