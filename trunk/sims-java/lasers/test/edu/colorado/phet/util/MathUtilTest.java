/**
 * Class: MathUtilTest
 * Package: edu.colorado.phet.util
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.util;

import junit.framework.TestCase;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.math.MathUtil;

public class MathUtilTest extends TestCase {

    public void testLinesIntersection() {

        // oblique lines
        Point2D.Float pt = MathUtil.getLinesIntersection( 2, 1, -1, 8 );
        assertTrue( MathUtil.isApproxEqual( (float)pt.getX(), 2f + (1f/3), 0.001f ));
        assertTrue( MathUtil.isApproxEqual( (float)pt.getY(), 5f + (2f/3), 0.001f ));

        // one horizontal line
        Point2D.Float pt2 = MathUtil.getLinesIntersection( 0, 1, -1, 8 );
        assertTrue( pt2.getX() == 7 );
        assertTrue( pt2.getY() == 1 );

    }

    public void testLinesInteresectin2() {

        // oblique lines
        Point2D pt = MathUtil.getLinesIntersection( new Point2D.Float( 0, 1 ),
                                                          new Point2D.Float( 2, 5 ),
                                                          new Point2D.Float( 10, -2 ),
                                                          new Point2D.Float( 5, 3 ));
        assertTrue( MathUtil.isApproxEqual( (float)pt.getX(), 2f + (1f/3), 0.001f ));
        assertTrue( MathUtil.isApproxEqual( (float)pt.getY(), 5f + (2f/3), 0.001f ));

        // one horizontal line
        Point2D pt2 = MathUtil.getLinesIntersection( new Point2D.Float( 0, 1 ),
                                                          new Point2D.Float( 10, 1 ),
                                                          new Point2D.Float( 1, 7 ),
                                                          new Point2D.Float( -20, 28 ));
        assertTrue( pt2.getX() == 7 );
        assertTrue( pt2.getY() == 1 );


    }
}
