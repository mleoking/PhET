/*
 * Class: SingleMoleculeTest
 * Package: edu.colorado.phet.controller
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.controller;

import edu.colorado.phet.idealgas.physics.HeavySpecies;
import edu.colorado.phet.idealgas.physics.LightSpecies;
import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.idealgas.physics.body.Particle;

/**
 *
 */
public class SpeedTest {


    public SpeedTest( IdealGasApplication application ) {

        application.init();

        double xOrigin = 132;
        double yOrigin = 202;
        double xDiag = 434;
        double yDiag = 397;

        Particle p1 = new HeavySpecies(
                new Vector2D( 250, 250 ),
                new Vector2D( 10, 10 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p1 );
        Particle p2 = new LightSpecies(
                new Vector2D( 250, 300 ),
                new Vector2D( 10, -10 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p2 );

        application.setClockParams( 0.1f, 20, 0.0f );


    }
}
