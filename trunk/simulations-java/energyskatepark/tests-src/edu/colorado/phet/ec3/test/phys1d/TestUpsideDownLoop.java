package edu.colorado.phet.ec3.test.phys1d;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 1, 2007
 * Time: 11:37:45 PM
 * Copyright (c) Mar 1, 2007 by Sam Reid
 */

public class TestUpsideDownLoop extends TestPhysics1D {

    public TestUpsideDownLoop() {
        getCubicSpline().setControlPoints( new Point2D.Double[]{new Point2D.Double( 4.560000000000002, 5.269999999999993 ), new Point2D.Double( 3.4899999999999984, 3.1999999999999953 ), new Point2D.Double( 4.999999999999996, 3.3499999999999908 ), new Point2D.Double( 3.1999999999999966, 5.2099999999999875 ), new Point2D.Double( 1.009999999999998, 2.1999999999999984 )} );
        getParticle().switchToTrack( getCubicSpline(), 0.92, true );
    }

    public static void main( String[] args ) {
        new TestUpsideDownLoop().start();
    }
}
