package edu.colorado.phet.common.phetcommon.math;

import junit.framework.TestCase;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class ZMathUtilTester extends TestCase {
    public void testLineToPointVectorClockwise() {
        Line2D line = new Line2D.Double(-1, -1, 1, 1);

        Point2D point = new Point2D.Double(1.0/MathUtil.SQRT_2, -1.0/MathUtil.SQRT_2);

        Vector2D vector = MathUtil.getClockwiseVectorFromLineToPoint(line, point);

        assertEquals(point.getX(), -vector.getX(), 0.00001);
        assertEquals(point.getY(), -vector.getY(), 0.00001);
    }

    public void testLineToPointVectorCounterClockwise() {
        Line2D line = new Line2D.Double(1, 1, -1, -1);

        Point2D point = new Point2D.Double(1.0/MathUtil.SQRT_2, -1.0/MathUtil.SQRT_2);

        Vector2D vector = MathUtil.getClockwiseVectorFromLineToPoint(line, point);

        assertEquals(point.getX(), -vector.getX(), 0.00001);
        assertEquals(point.getY(), -vector.getY(), 0.00001);
    }
}
