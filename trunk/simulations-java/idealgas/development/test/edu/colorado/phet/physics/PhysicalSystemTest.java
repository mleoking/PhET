/*
 * Class: PhysicalSystemTest
 * Package: edu.colorado.phet.physicaldomain
 *
 * Created by: Ron LeMaster
 * Date: Oct 22, 2002
 */
package edu.colorado.phet.physics;

import junit.framework.TestCase;
import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.physics.collision.CollisionLaw;

/**
 *
 */
public class PhysicalSystemTest extends TestCase {

    public PhysicalSystemTest() {
        super("");
    }

    public PhysicalSystemTest( String s ) {
        super( s );
    }

    /**
     * Put a single particle and force in the context, and see if the particle
     * accelerates and the context steps properly in time
     */
    public void testExternalForce2D() {

        PhysicalSystem system = new PhysicalSystem( new IdealGasConfig() );

        Particle p1 = new Particle(
                new Vector2D( 100, 100 ),
                new Vector2D( 0, 0 ),
                new Vector2D( 0, 0 ),
                100 );

        ExternalForce2D force = new ExternalForce2D();
        force.setX( 10 );
        force.setY( 5 );

        system.addBody( p1 );
        system.addExternalForce( force );
        system.setClockParams( 0.1f, 20, 0.1f );
        system.run();

        System.out.println( "--> p1: " + p1 );
    }

    /**
     * CollisionGod the CollisionLaw on some particles
     */
    public void testCollisionLaw() {
        PhysicalSystem system = new PhysicalSystem( new IdealGasConfig() );

        Particle p1 = new Particle(
                new Vector2D( 100, 100 ),
                new Vector2D( 50, 0 ),
                new Vector2D( 0, 0 ),
                10 );
        system.addBody( p1 );

        Particle p2 = new Particle(
                new Vector2D( 101, 100 ),
                new Vector2D( 0, 0 ),
                new Vector2D( 0, 0 ),
                10 );
        system.addBody( p2 );

        system.addLaw( CollisionLaw.instance() );
        system.run();
    }

    //
    // Static members and methods
    //
    public static void main( String[] args ) {
        PhysicalSystemTest test = new PhysicalSystemTest();
        test.testCollisionLaw();
    }
}
