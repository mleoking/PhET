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
import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.MustContain;
import edu.colorado.phet.idealgas.physics.BoxMustContainParticle;
import edu.colorado.phet.physics.Constraint;

/**
 *
 */
public class SingleMoleculeTest {


    public SingleMoleculeTest( IdealGasApplication application ) {

        application.init();

        double xOrigin = 132;
        double yOrigin = 202;
        double xDiag = 434;
        double yDiag = 397;

        Particle p1 = new HeavySpecies(
                //new Vector2D( 529, 206.78431367909573 ),
                //new Vector2D( -255, -220 ),

                //new Vector2D( 380, 280 ),
                //new Vector2D( 255, -220 ),

                //new Vector2D( 518.045454083544, 216.23529410326057 ),
                //new Vector2D( 255, -220 ),

                //new Vector2D( 251.9642321293154, 237.99590014286304 ),
                //new Vector2D( -255, 220 ),

                new Vector2D( 300, 250 ),
                new Vector2D( 0, 0 ),

//                new Vector2D( 534.3791672284128, 367.39537807069166 ),
//                new Vector2D( -36.1246684895856, -25.585076100393255 ),

                new Vector2D( 0, 0 ),

                10);
        application.addBody( p1 );
        Box2D box = application.getIdealGasSystem().getBox();
        Constraint constraintSpec = new BoxMustContainParticle( box, p1 );
        p1.addConstraint( constraintSpec );

        application.setClockParams( 0.1f, 20, 0.0f );


    }
}
