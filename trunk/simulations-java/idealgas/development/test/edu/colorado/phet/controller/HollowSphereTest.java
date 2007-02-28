/*
 * Class: SingleMoleculeTest
 * Package: edu.colorado.phet.controller
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.controller;

import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.idealgas.physics.*;
import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.physics.*;
import edu.colorado.phet.idealgas.physics.body.HollowSphere;
import edu.colorado.phet.physics.collision.Box2D;

/**
 *
 */
public class HollowSphereTest {


    public HollowSphereTest( IdealGasApplication application ) {
        setup4( application );
    }

    /**
     *
     * @param application
     */
    private void setup3( IdealGasApplication application ) {

        HollowSphere sphere = new HollowSphere(
                new Vector2D( 300, 350 ),
                new Vector2D( 20, -10 ),
                new Vector2D( 0, 0 ),
                100,
                50 );
        application.addBody( sphere );
    }

    /**
     *
     * @param application
     */
    private void setup2( IdealGasApplication application ) {

        HollowSphere sphere = new HollowSphere(
                new Vector2D( 300, 350 ),
                new Vector2D( 0, 0 ),
                new Vector2D( 0, 0 ),
                100,
                50 );
        application.addBody( sphere );

        Particle p1 = null;
        int num = 4;
        for( int i = 1; i <= num; i++ ) {
            for( int j = 0; j < num; j++ ) {
                p1 = new LightSpecies(
                        new Vector2D( 280 + i * 10, 330 + j * 10 ),
                        new Vector2D( i * 12, i * 12 ),
                        new Vector2D( 0, 0 ),
                        10 );
                sphere.addContainedBody( p1 );
                application.addBody( p1 );
            }
        }
    }

    private void setup4( IdealGasApplication application ) {
        float xOrigin = 200;
        float yOrigin = 250;
        float xDiag = 434;
        float yDiag = 397;

        Box2D box = application.getIdealGasSystem().getBox();

        HollowSphere sphere = new HollowSphere(
                new Vector2D( 300, 350 ),
                new Vector2D( 0, 0 ),
                new Vector2D( 0, 0 ),
                100,
                50 );
        application.addBody( sphere );
        Constraint constraintSpec = new BoxMustContainParticle( box, sphere );
        sphere.addConstraint( constraintSpec );

        for( int i = 0; i < 50; i++ ) {
            float x = (float)Math.random() * ( xDiag - xOrigin - 20 ) + xOrigin + 50;
            float y = (float)Math.random() * ( yDiag - yOrigin - 20 ) + yOrigin + 10;
            float vx = (float)Math.random() * 50;
            float vy = (float)Math.random() * 50;
            //float m = Math.random() * 20;
            float m = 10;
            //Particle p1 = new Particle(
            Particle p1 = new HeavySpecies(
                    new Vector2D( x, y ),
                    new Vector2D( vx, vy ),
                    new Vector2D( 0, 0 ),
                    m );
            application.addBody( p1 );
            constraintSpec = new BoxMustContainParticle( box, p1 );
            p1.addConstraint( constraintSpec );

            constraintSpec = new HollowSphereMustNotContainParticle( sphere, p1 );
            p1.addConstraint( constraintSpec );
        }

        Particle p1 = null;
//        int num = 0;
        int num = 4;
        int sign = 1;
        for( int i = 1; i <= num; i++ ) {
            for( int j = 0; j < num; j++ ) {
                sign *= -1;
                p1 = new LightSpecies(
                        new Vector2D( 280 + i * 10, 330 + j * 10 ),
                        new Vector2D( sign * i * 12, sign * i * 12 ),
                        new Vector2D( 0, 0 ),
                        10 );
                sphere.addContainedBody( p1 );
                application.addBody( p1 );

                constraintSpec = new BoxMustContainParticle( box, p1 );
                p1.addConstraint( constraintSpec );

                constraintSpec = new HollowSphereMustContainParticle( sphere, p1 );
                p1.addConstraint( constraintSpec );
            }
        }
    }

    /**
     *
     * @param application
     */
    private void setup1( IdealGasApplication application ) {
        HollowSphere sphere = new HollowSphere(
                new Vector2D( 300, 350 ),
                new Vector2D( 0, 0 ),
                new Vector2D( 0, 0 ),
                50,
                50 );
        application.addBody( sphere );

        Particle p1 = new HeavySpecies(
                new Vector2D( 300, 340 ),
                new Vector2D( 0, 20 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p1 );

        p1 = new HeavySpecies(
                new Vector2D( 280, 360 ),
                new Vector2D( -10, 20 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p1 );

        p1 = new HeavySpecies(
                new Vector2D( 310, 290 ),
                new Vector2D( 10, -20 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p1 );

    }

}
