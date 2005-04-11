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
import edu.colorado.phet.idealgas.physics.LightSpecies;
import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.physics.*;
import edu.colorado.phet.physics.collision.HorizontalWall;
import edu.colorado.phet.idealgas.physics.body.Particle;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 */
public class WallParticleCollisionTest {

    public WallParticleCollisionTest( IdealGasApplication application ) {

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
                new Vector2D( 20, -20 ),
                new Vector2D( 0, 0 ),
                10 );
        application.addBody( p1 );

        HorizontalWall horizontalWall = new HorizontalWall( 225, 425, 350, 350, HorizontalWall.FACING_UP );
        application.addBody( horizontalWall );
        //Particle p2 = new LightSpecies(
        //        new Vector2D( 270, 262 ),
        //        new Vector2D( -3, 0 ),
        //        new Vector2D( 0, 0 ),
        //        10 );
        //application.addBody( p2 );

        application.setClockParams( 0.1f, 20, 0.0f );
    }
}
