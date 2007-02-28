/*
 * Class: SingleMoleculeTest
 * Package: edu.colorado.phet.controller
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.controller;

import edu.colorado.phet.idealgas.physics.PressureSensingBox;
import edu.colorado.phet.idealgas.physics.HeavySpecies;
import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.physics.Gravity;
import edu.colorado.phet.physics.collision.CollisionLaw;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 */
public class CollisionTest {

    public CollisionTest( IdealGasApplication application ) {

        application.init();

        double xOrigin = 132;
        double yOrigin = 202;
        double xDiag = 434;
        double yDiag = 397;

//        Particle p1 = new HeavySpecies(
//                new Vector2D( 280, 270 ),
//                new Vector2D( 5, 3 ),
//                new Vector2D( 0, 0 ),
//                10 );
//        application.addBody( p1 );
//
//        Particle p2 = new HeavySpecies(
//                new Vector2D( 300, 250 ),
//                new Vector2D( 1, 5 ),
//                new Vector2D( 0, 0 ),
//                10 );
//        application.addBody( p2 );

//        Particle p1 = new HeavySpecies(
//                new Vector2D( 250, 250 ),
//                new Vector2D( 3, 3 ),
//                new Vector2D( 0, 0 ),
//                10 );
//        application.addBody( p1 );
//
//        Particle p2 = new HeavySpecies(
//                new Vector2D( 300, 250 ),
//                new Vector2D( -3, 3 ),
//                new Vector2D( 0, 0 ),
//                10 );
//        application.addBody( p2 );


        Particle p1 = new HeavySpecies(
                new Vector2D( 250, 260 ),
                new Vector2D( 0, -3 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p1 );

        Particle p2 = new HeavySpecies(
                new Vector2D( 260, 230 ),
                new Vector2D( -3, 3 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p2 );

        application.setClockParams( 0.1f, 20, 0.0f );


    }
}
