package edu.colorado.phet.energyskatepark.model.physics;

import edu.colorado.phet.timeseries.SPoint2D;
import junit.framework.TestCase;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 17, 2007
 * Time: 6:34:17 PM
 * Copyright (c) Feb 17, 2007 by Sam Reid
 */

public class TestCubicSpline2D extends TestCase {

    public void testAngle() {
        ParametricFunction2D cubicSpline = new CubicSpline2D( new Point2D[]{
                new SPoint2D.Double( 0, 0 ),
                new SPoint2D.Double( 1, 0 )
        } );
        assertEquals( "angle should be equal", 0, cubicSpline.getAngle( 0.5 ), 1E-6 );
    }

    public void testAngleSlope() {
        ParametricFunction2D cubicSpline = new CubicSpline2D( new Point2D[]{
                new SPoint2D.Double( 0, 0 ),
                new SPoint2D.Double( 1, 1 )
        } );
        assertEquals( "angle should be equal", Math.PI * 1 / 4, cubicSpline.getAngle( 0.5 ), 1E-6 );//is arc direction ok?
    }

    public void testSplineLength() {
        ParametricFunction2D cubicSpline = new CubicSpline2D( new Point2D[]{
                new SPoint2D.Double( 0, 0 ),
                new SPoint2D.Double( 1, 0 )
        } );
        assertEquals( "Length should be equal", 1.0, cubicSpline.getMetricDelta( 0, 1 ), 1E-6 );//is arc direction ok?
    }

    public void testSplineLength2() {
        ParametricFunction2D cubicSpline = new CubicSpline2D( new Point2D[]{
                new SPoint2D.Double( 0, 0 ),
                new SPoint2D.Double( 1, 1 )
        } );
        assertEquals( "Length should be equal", Math.sqrt( 2 ), cubicSpline.getMetricDelta( 0, 1 ), 1E-6 );//is arc direction ok?
    }

    public void testSplineLength3() {
        ParametricFunction2D cubicSpline = new CubicSpline2D( new Point2D[]{
                new SPoint2D.Double( 0, 0 ),
                new SPoint2D.Double( 100, 0 )
        } );
        assertEquals( "Length should be equal", 10, cubicSpline.getMetricDelta( 0.1, 0.2 ), 1E-6 );//is arc direction ok?
    }
}
