// Copyright 2002-2011, University of Colorado

/**
 * Class: WallSphereContactExpertTest
 * Package: edu.colorado.phet.lasers.model.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.test;

import junit.framework.TestCase;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.collision.SphericalBody;
import edu.colorado.phet.common.collision.Wall;

public class WallSphereContactExpertTest extends TestCase {

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
        Wall wall = new Wall( new Rectangle2D.Double( 0, 0, 0, 100 ) );
        SphericalBody sb = new SphericalBody( 10 );
        sb.setPosition( 5, 5 );

//        SphereWallContactDetector wsce = new SphereWallContactDetector();
//        boolean b = false;
//        b = wsce.areInContact( wall, sb );
//        assertTrue( b );
//        b = wsce.areInContact( sb, wall );
//        assertTrue( b );
    }

    /**
     * Test simple non-contact
     */
    public void test2() {
        Wall wall = new Wall( new Rectangle2D.Double( 0, 0, 0, 100 ) );
        SphericalBody sb = new SphericalBody( 10 );
        sb.setPosition( 12, 5 );

//        SphereWallContactDetector wsce = new SphereWallContactDetector();
//        boolean b = false;
//        b = wsce.areInContact( wall, sb );
//        assertTrue( !b );
//        b = wsce.areInContact( sb, wall );
//        assertTrue( !b );
    }

    /**
     * Test for contact beyond extent of wall
     */
    public void test3() {
        Wall wall = new Wall( new Rectangle2D.Double( 0, 0, 0, 100 ) );
        SphericalBody sb = new SphericalBody( 10 );
        sb.setPosition( 5, -10 );

//        SphereWallContactDetector wsce = new SphereWallContactDetector();
//        boolean b = false;
//        b = wsce.areInContact( wall, sb );
//        assertTrue( !b );
//        b = wsce.areInContact( sb, wall );
//        assertTrue( !b );
    }
}
