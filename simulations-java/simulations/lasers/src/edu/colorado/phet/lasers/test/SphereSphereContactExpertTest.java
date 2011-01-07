// Copyright 2002-2011, University of Colorado

/**
 * Class: WallSphereContactExpertTest
 * Package: edu.colorado.phet.lasers.model.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.lasers.test;

import junit.framework.TestCase;

import edu.colorado.phet.common.collision.SphereSphereContactDetector;
import edu.colorado.phet.common.collision.SphericalBody;

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
