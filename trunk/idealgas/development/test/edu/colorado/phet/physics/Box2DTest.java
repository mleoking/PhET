/*
 * Class: Box2DTest
 * Package: edu.colorado.phet.physicaldomain
 *
 * Created by: Ron LeMaster
 * Date: Oct 25, 2002
 */
package edu.colorado.phet.physics;

import junit.framework.TestCase;
import edu.colorado.phet.physics.collision.Box2D;

/**
 *
 */
public class Box2DTest extends TestCase {


    public Box2DTest () {
        this( "" );
    }

    public Box2DTest ( String s ) {
        super( s );
    }

    //
    // CollisionGod methods
    //
    public void testGetClosestCorner1() {
        Vector2D result;
        Vector2D corner1 = new Vector2D( 50, 50 );
        Vector2D corner2 = new Vector2D( 150, 200 );
        Vector2D corner3 = new Vector2D( 50, 200 );
        Vector2D corner4 = new Vector2D( 150, 50 );
        Box2D box = new Box2D( corner1, corner2 );

        Vector2D pt1 = new Vector2D( 60, 70 );
        result = box.getClosestCorner( pt1 );
        assertEquals( result, corner1 );

        Vector2D pt2 = new Vector2D( 80, 167 );
        result = box.getClosestCorner( pt2 );
        assertEquals( result, corner3 );

        Vector2D pt3 = new Vector2D( 130, 80 );
        result = box.getClosestCorner( pt3 );
        assertEquals( result, corner4 );

        Vector2D pt4 = new Vector2D( 130, 155 );
        result = box.getClosestCorner( pt4 );
        assertEquals( result, corner2 );
    }

    public void testGetClosestCorner2() {
        Vector2D result;
        Vector2D corner1 = new Vector2D( 50, 50 );
        Vector2D corner2 = new Vector2D( 150, 200 );
        Vector2D corner3 = new Vector2D( 50, 200 );
        Vector2D corner4 = new Vector2D( 150, 50 );
        Box2D box = new Box2D( corner2, corner1 );

        Vector2D pt1 = new Vector2D( 60, 70 );
        result = box.getClosestCorner( pt1 );
        assertEquals( result, corner1 );

        Vector2D pt2 = new Vector2D( 80, 167 );
        result = box.getClosestCorner( pt2 );
        assertEquals( result, corner3 );

        Vector2D pt3 = new Vector2D( 130, 80 );
        result = box.getClosestCorner( pt3 );
        assertEquals( result, corner4 );

        Vector2D pt4 = new Vector2D( 130, 155 );
        result = box.getClosestCorner( pt4 );
        assertEquals( result, corner2 );
    }

    public void testGetClosestCorner3() {
        Vector2D result;
        Vector2D pt;
        Vector2D corner1 = new Vector2D( 50, 50 );
        Vector2D corner2 = new Vector2D( -150, 200 );
        Vector2D corner3 = new Vector2D( 50, 200 );
        Vector2D corner4 = new Vector2D( -150, 50 );
        Box2D box = new Box2D( corner2, corner1 );

        pt = new Vector2D( 10, 70 );
        result = box.getClosestCorner( pt );
        assertEquals( result, corner1 );

        pt = new Vector2D( -10, 70 );
        result = box.getClosestCorner( pt );
        assertEquals( result, corner1 );

        pt = new Vector2D( 30, 180 );
        result = box.getClosestCorner( pt );
        assertEquals( result, corner3 );

        pt = new Vector2D( -30, 155 );
        result = box.getClosestCorner( pt );
        assertEquals( result, corner3 );

        pt = new Vector2D( -130, 155 );
        result = box.getClosestCorner( pt );
        assertEquals( result, corner2 );
    }

    //
    // Static fields and methods
    //
    public static void main( String[]  args ) {
        Box2DTest test = new Box2DTest();
        test.testGetClosestCorner1();
        test.testGetClosestCorner2();
        test.testGetClosestCorner3();
    }
}
