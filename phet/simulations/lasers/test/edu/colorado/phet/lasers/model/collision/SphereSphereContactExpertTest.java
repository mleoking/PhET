/**
 * Class: WallSphereContactExpertTest
 * Package: edu.colorado.phet.lasers.model.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.model.collision;

import edu.colorado.phet.collision.SphericalBody;
import edu.colorado.phet.collision.SphereSphereContactDetector;

import java.awt.geom.Point2D;

import junit.framework.TestCase;

public class SphereSphereContactExpertTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
//        new LaserSystem();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
//        PhysicalSystem.setInstanceNull();
    }

    /**
     * Tests simple contact
     */
    public void test1() {
        SphericalBody sb1 = new SphericalBody( 5 );
        sb1.setPosition( 5, 5 );
        SphericalBody sb2 = new SphericalBody( 5 );
        sb2.setPosition( 7, 12 );

        SphereSphereContactDetector wsce = new SphereSphereContactDetector();
        boolean b = false;
        b = wsce.areInContact( sb1, sb2 );
        assertTrue( b );
        b = wsce.areInContact( sb2, sb1 );
        assertTrue( b );
    }

    /**
     * Test simple non-contact
     */
    public void test2() {
        SphericalBody sb1 = new SphericalBody( 5 );
        sb1.setPosition( 5, 5 );
        SphericalBody sb2 = new SphericalBody( 5 );
        sb2.setPosition( 12, 13 );

        SphereSphereContactDetector wsce = new SphereSphereContactDetector();
        boolean b = false;
        b = wsce.areInContact( sb1, sb2 );
        assertTrue( !b );
        b = wsce.areInContact( sb2, sb1 );
        assertTrue( !b );
    }
}
