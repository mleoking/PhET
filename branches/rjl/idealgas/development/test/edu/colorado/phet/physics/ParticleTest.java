/*
 * Class: ParticleTest
 * Package: edu.colorado.phet.physicaldomain
 *
 * Created by: Ron LeMaster
 * Date: Oct 22, 2002
 */
package edu.colorado.phet.physics;

import junit.framework.TestCase;
import edu.colorado.phet.idealgas.physics.body.Particle;

/**
 *
 */
public class ParticleTest extends TestCase {

    public ParticleTest() {
        this("");
    }

    public ParticleTest( String s ) {
        super( s );
    }

    public void testContact() {
        Particle p1 = new Particle(
                new Vector2D( 10, 10 ),
                new Vector2D( 0, 0 ),
                new Vector2D( 0, 0 ),
                0, 0 );

        Particle p2 = new Particle(
                new Vector2D( 10, 11 ),
                new Vector2D( 0, 0 ),
                new Vector2D( 0, 0 ),
                0, 0 );

        Particle p3 = new Particle(
                new Vector2D( 10, 12 ),
                new Vector2D( 0, 0 ),
                new Vector2D( 0, 0 ),
                0, 0 );

        Particle p4 = new Particle(
                new Vector2D( 10, 13 ),
                new Vector2D( 0, 0 ),
                new Vector2D( 0, 0 ),
                0, 0 );

        assertTrue( p1.isInContactWithBody( p2) );
        assertTrue( p1.isInContactWithBody( p3) );
        assertTrue( !p1.isInContactWithBody( p4) );
    }

    public static void main( String[] args ) {
    }
}
