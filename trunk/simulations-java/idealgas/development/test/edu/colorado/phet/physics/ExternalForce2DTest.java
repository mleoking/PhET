/*
 * Class: ExternalForce2DTest
 * Package: edu.colorado.phet.physicaldomain
 *
 * Created by: Ron LeMaster
 * Date: Oct 21, 2002
 */
package edu.colorado.phet.physics;

import junit.framework.TestCase;
import edu.colorado.phet.idealgas.physics.body.Particle;

/**
 *
 */
public class ExternalForce2DTest extends TestCase {

    public ExternalForce2DTest() {
        super("");
    }

    public ExternalForce2DTest( String s ) {
        super( s );
    }

    public void testAct() {

        Particle p1 = new Particle(
                new Vector2D( 100, 100 ),
                new Vector2D( 0, 0 ),
                new Vector2D( 0, 0 ),
                100 );

        ExternalForce2D force = new ExternalForce2D();
        force.setX( 10 );
        force.setY( 5 );

        force.act( p1 );
        System.out.println( "--> " + p1.getAcceleration().getX() + "   " + p1.getAcceleration().getY() );

        Particle p2 = new Particle(
                new Vector2D( 100, 100 ),
                new Vector2D( 0, 0 ),
                new Vector2D( 1, 2 ),
                100 );
        force.act( p2 );
        System.out.println( "--> " + p2.getAcceleration().getX() + "   " + p2.getAcceleration().getY() );

    }


    //
    // Static attributes and methods
    //
    public static void main( String[] args ) {

        ExternalForce2DTest test = new ExternalForce2DTest();
        test.testAct();
    }

}
