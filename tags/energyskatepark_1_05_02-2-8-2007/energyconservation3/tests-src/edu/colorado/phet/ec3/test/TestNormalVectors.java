package edu.colorado.phet.ec3.test;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import junit.framework.TestCase;

import java.util.Random;

/**
 * User: Sam Reid
 * Date: Oct 17, 2006
 * Time: 1:09:08 AM
 * Copyright (c) Oct 17, 2006 by Sam Reid
 */

public class TestNormalVectors extends TestCase {
    public void testUniform() {
        for( double x = -10; x <= 10; x += 3 ) {
            for( double y = -10; y <= 10; y += 3 ) {
                testNormalVectors( x, y );
            }
        }
    }

    public void testRandom() {
        Random random = new Random();
        for( int i = 0; i < 100; i++ ) {
            testNormalVectors( ( random.nextDouble() - 0.5 ) * 100.0, ( random.nextDouble() - 0.5 ) * 100.0 );
        }
    }

    public void testSquareVectors() {
        testNormalVectors( 1, 1 );
        testNormalVectors( 1, -1 );
        testNormalVectors( -1, -1 );
        testNormalVectors( -1, 1 );
    }

    private static double testNormalVectors( double x, double y ) {
        Vector2D.Double a = new Vector2D.Double( x, y );
        AbstractVector2D rotated = a.getRotatedInstance( -Math.PI / 2.0 );

        AbstractVector2D normed = a.getNormalVector();
        System.out.println( "original vector=" + a + ", rotated vector= " + rotated + ", getNormalVector=" + normed );
        double error = rotated.getSubtractedInstance( normed ).getMagnitude();
        assertEquals( error, 0.0, 1E-6 );
        return error;
    }
}
