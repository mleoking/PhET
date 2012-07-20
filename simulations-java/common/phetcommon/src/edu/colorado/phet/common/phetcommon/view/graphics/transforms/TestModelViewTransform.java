// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon.view.graphics.transforms;

import junit.framework.TestCase;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

public class TestModelViewTransform extends TestCase {
    public static double EPSILON = 0.000001;

    public void testIdentity() {
        ModelViewTransform t = ModelViewTransform.createIdentity();
        Vector2D zero = new Vector2D( 0, 0 );
        assertEpsilonEquals( zero, t.modelToView( zero ) );
        assertEpsilonEquals( zero, t.viewToModel( zero ) );
        assertEpsilonEquals( zero, t.modelToViewDelta( zero ) );
        assertEpsilonEquals( zero, t.viewToModelDelta( zero ) );
        Vector2D something = new Vector2D( -2.1, 5.4 );
        assertEpsilonEquals( something, t.modelToView( something ) );
        assertEpsilonEquals( something, t.viewToModel( something ) );
        assertEpsilonEquals( something, t.modelToViewDelta( something ) );
        assertEpsilonEquals( something, t.viewToModelDelta( something ) );
    }

    public void testOffsetScale() {
        int tX = 3;
        int tY = 5;
        int m = 2;
        ModelViewTransform t = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( tX, tY ), m );
        int aX = -3;
        int aY = 7;
        Vector2D a = new Vector2D( aX, aY );
        Vector2D aTransformed = new Vector2D( aX * m + tX, aY * m + tY );
        Vector2D aInverted = new Vector2D( ( aX - tX ) / m, ( aY - tY ) / m );
        assertEpsilonEquals( aTransformed, t.modelToView( a ) );
        assertEpsilonEquals( aInverted, t.viewToModel( a ) );
        assertEpsilonEquals( a, t.viewToModel( t.modelToView( a ) ) );
        assertEpsilonEquals( aTransformed, t.viewToModel( t.modelToView( aTransformed ) ) );
    }

    public void testRectangleMapping() {
        ModelViewTransform t = ModelViewTransform.createRectangleMapping(
                new Rectangle2D.Double( -1, 1, 4, 2 ),
                new Rectangle2D.Double( 4, 7, 8, 1 )
        );

    }

    public void testSinglePointScaleInvertedYMapping() {
        ModelViewTransform t1 = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( 40, 30 ), 2 );
        assertEpsilonEquals( t1.modelToView( new Vector2D() ), new Vector2D( 40, 30 ) );
        assertEpsilonEquals( t1.viewToModel( new Vector2D( 40, 30 ) ), new Vector2D() ); // test inverted case
        assertEpsilonEquals( t1.modelToView( new Vector2D( 10, 10 ) ), new Vector2D( 60, 10 ) );
    }

    public void assertEpsilonEquals( double a, double b ) {
        assertEquals( a, b, EPSILON );
    }

    public void assertEpsilonEquals( Vector2D a, Vector2D b ) {
        assertEpsilonEquals( a.getX(), b.getX() );
        assertEpsilonEquals( a.getY(), b.getY() );
    }
}
