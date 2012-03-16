// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import junit.framework.TestCase;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class ZMathUtilTester extends TestCase {
    private static final Line2D.Double HORIZONTAL_LINE = new Line2D.Double( 0, 0, 1, 0 );
    private static final Line2D.Double VERTICAL_LINE = new Line2D.Double( 0, 0, 0, 1 );
    private static final double EPS = 0.00001;

    public void testLineToPointVector() {
        Line2D line = new Line2D.Double( -1, -1, 1, 1 );

        Point2D point = new Point2D.Double( 1.0 / MathUtil.SQRT_2, -1.0 / MathUtil.SQRT_2 );

        Vector2D vector = MathUtil.getVectorFromLineToPoint( line, point );

        assertEquals( point.getX(), vector.getX(), EPS );
        assertEquals( point.getY(), vector.getY(), EPS );
    }

    public void testGetPointOnLineClosestToPoint1() {
        Point2D point = MathUtil.getPointOnLineClosestToPoint( HORIZONTAL_LINE, new Point2D.Double( 0.5, 0.5 ) );

        assertEquals( 0.5, point.getX(), EPS );
        assertEquals( 0.0, point.getY(), EPS );
    }

    public void testGetPointOnLineClosestToPoint2() {
        Point2D point = MathUtil.getPointOnLineClosestToPoint( VERTICAL_LINE, new Point2D.Double( 0.5, 0.5 ) );

        assertEquals( 0.5, point.getY(), EPS );
        assertEquals( 0.0, point.getX(), EPS );
    }
}
