/*
 * Class: SingleMoleculeTest
 * Package: edu.colorado.phet.controller
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.controller;

import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.idealgas.physics.HeavySpecies;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.idealgas.physics.body.HollowSphere;

/**
 *
 */
public class TwoParticleCollisionTest {

    public TwoParticleCollisionTest( IdealGasApplication application ) {

        application.init();
        application.setClockParams( 0.1f, 20, 0.0f );

//        gravitySetup( application );
        setup1( application );
    }

    private void gravitySetup( IdealGasApplication application ) {
        Particle p1 = new HeavySpecies(
                new Vector2D( 250, 300),
                new Vector2D( 0, -20 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p1 );

        Particle p2 = new HeavySpecies(
                new Vector2D( 250, 320 ),
                new Vector2D( 0, 20 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p2 );

    }

    private void setup1( IdealGasApplication application ) {
        Particle p1 = new HeavySpecies(
                new Vector2D( 400, 430),
                new Vector2D( -20, 1 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p1 );

        Particle p2 = new HeavySpecies(
                new Vector2D( 400, 434 ),
                new Vector2D( -20, -1 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p2 );

//        Particle p3 = new HeavySpecies(
//                new Vector2D( 245, 435 ),
//                new Vector2D( -15, 5 ),
//                new Vector2D( 0, 0 ),
//                10 );
//        application.addBody( p3 );
//
    }

    private void twoParticlesInCorner( IdealGasApplication application ) {

        Particle p1 = new HeavySpecies(
                new Vector2D( 250, 430),
                new Vector2D( -20, 10 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p1 );

        Particle p2 = new HeavySpecies(
                new Vector2D( 240, 440 ),
                new Vector2D( 15, 15 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p2 );
    }

    private void particleEmbeddedInWall( IdealGasApplication application ) {
        double xOrigin = 132;
        double yOrigin = 202;
        double xDiag = 434;
        double yDiag = 397;

        Particle p1 = new HeavySpecies(
                new Vector2D( 534.3791672284128f, 367.39537807069166f ),
                new Vector2D( -36.1246684895856f, -25.585076100393255f ),

                new Vector2D( 0, 0 ),

                10 );
        application.addBody( p1 );
    }
}
