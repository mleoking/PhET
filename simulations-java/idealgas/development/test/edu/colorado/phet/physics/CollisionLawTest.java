/*
 * Class: CollisionLawTest
 * Package: edu.colorado.phet.physicaldomain
 *
 * Created by: Ron LeMaster
 * Date: Oct 23, 2002
 */
package edu.colorado.phet.physics;

import junit.framework.TestCase;
import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.physics.collision.CollisionLaw;

/**
 *
 */
public class CollisionLawTest extends TestCase {


    public CollisionLawTest () {
        this( "" );
    }

    public CollisionLawTest ( String s ) {
        super( s );
    }

    //
    // CollisionGod methods
    //

    /**
     * Tests a collision in which the line of action is the x axis, and the masses and initial velocities of the
     * two particles are unequal and non-zero.
     */
    public void testParticleToParticleCollisionA() {

        Particle p1 = new Particle(
                new Vector2D( 100, 100 ),
                new Vector2D( 10, 0 ),
                new Vector2D( 0, 0 ),
                6 );

        Particle p2 = new Particle(
                new Vector2D( 101, 100 ),
                new Vector2D( -5, 0 ),
                new Vector2D( 0, 0 ),
                4 );

        CollisionLaw law = CollisionLaw.instance();
        law.apply( p1, p2 );
        assertTrue( p1.getVelocity().getX() == -2 );
        assertTrue( p1.getVelocity().getY() == 0 );
        assertTrue( p2.getVelocity().getX() == 13 );
        assertTrue( p2.getVelocity().getY() == 0 );
    }

    /**
     * Tests a collision in which the line of action is the y axis, and the masses and initial velocities of the
     * two particles are unequal and non-zero.
     */
    public void testParticleToParticleCollisionB() {

        Particle p1 = new Particle(
                new Vector2D( 100, 100 ),
                new Vector2D( 0, 10 ),
                new Vector2D( 0, 0 ),
                6 );

        Particle p2 = new Particle(
                new Vector2D( 100, 101 ),
                new Vector2D( 0, -5 ),
                new Vector2D( 0, 0 ),
                4 );

        CollisionLaw law = CollisionLaw.instance();
        law.apply( p1, p2 );
        assertTrue( p1.getVelocity().getX() == 0 );
        assertTrue( p1.getVelocity().getY() == -2 );
        assertTrue( p2.getVelocity().getX() == 0 );
        assertTrue( p2.getVelocity().getY() == 13 );
    }


    /**
     * Tests a collision in which the line of action is at 45 degrees, and the masses and initial velocities of the
     * two particles are eqaul, and one particle is at rest
     */
    public void testParticleToParticleCollisionC() {

        Particle p1 = new Particle(
                new Vector2D( 99.7f, 99.7f ),
                new Vector2D( 0, 10 ),
                new Vector2D( 0, 0 ),
                4 );

        Particle p2 = new Particle(
                new Vector2D( 100.4f, 100.4f ),
                new Vector2D( 0, 0 ),
                new Vector2D( 0, 0 ),
                2 );

        CollisionLaw law = CollisionLaw.instance();
        law.apply( p1, p2 );
        double roundOff = 0.0001;
        assertTrue( Math.abs( p1.getVelocity().getX() - ( -10.0 / 3.0 )) < roundOff );
        assertTrue( Math.abs( p1.getVelocity().getY() - ( 20.0 / 3.0 )) < roundOff );
        assertTrue( Math.abs( p2.getVelocity().getX() - ( 20.0 / 3.0 )) < roundOff );
        assertTrue( Math.abs( p2.getVelocity().getY() - ( 20.0 / 3.0 )) < roundOff );
    }

    /**
     * CollisionGod simple particle to box collision
     */
    public void testParticleToBoxCollisionA() {

        Box2D box = new Box2D(
                new Vector2D( 100, 100 ),
                new Vector2D( 200, 200 ));

        Particle p = new Particle(
                new Vector2D( 197, 110 ),
                new Vector2D( 10, 0 ),
                new Vector2D( 0, 0 ),
                4 );

        CollisionLaw law = CollisionLaw.instance();
        law.apply( p, box );
        System.out.println( "p: " + p );
        double roundOff = 0.0001;
        assertTrue( Math.abs( p.getVelocity().getX() - (-10) ) < roundOff );

    }

    //
    // Static fields and methods
    //
    public static void main( String[]  args ) {
        CollisionLawTest test = new CollisionLawTest();
        test.testParticleToParticleCollisionA();
        test.testParticleToParticleCollisionB();
        test.testParticleToParticleCollisionC();
        test.testParticleToBoxCollisionA();
    }


}
