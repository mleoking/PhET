/**
 * Class: Level1
 * Package: edu.colorado.phet.distanceladder.levels
 * Author: Another Guy
 * Date: Apr 16, 2004
 */
package edu.colorado.phet.distanceladder.levels;

import edu.colorado.phet.distanceladder.controller.CockpitModule;
import edu.colorado.phet.distanceladder.controller.StarMapModule;
import edu.colorado.phet.distanceladder.exercise.Answer;
import edu.colorado.phet.distanceladder.exercise.Exercise;
import edu.colorado.phet.distanceladder.exercise.XmlExercise;
import edu.colorado.phet.distanceladder.model.NormalStar;
import edu.colorado.phet.distanceladder.model.Star;
import edu.colorado.phet.distanceladder.model.UniverseModel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Similar to Level3A, but the starship coordinate graphic is disabled on the star maps
 */
public class Level3B extends Exercise {

    private static Point2D.Double starPt = new Point2D.Double( 2000, 1500 );
    private static Point2D.Double targetPt = new Point2D.Double( -1600, 800 );

    private Star[] starField = new Star[]{
        new NormalStar( Color.green, 1E6, new Point2D.Double( -900, -300 ), 20 ),
        new NormalStar( Color.yellow, 1E6, new Point2D.Double( 200, 700 ), -10 ),
        new NormalStar( Color.green, 1E6, new Point2D.Double( 800, -200 ), -15 ),
        new NormalStar( Color.yellow, 1E6, new Point2D.Double( 550, -100 ), 15 ),
        new NormalStar( Color.red, 5E5, targetPt, 0 )
    };

    public Level3B( JFrame frame, final UniverseModel model,
                    CockpitModule cockpitModule, StarMapModule starMapModule ) {
        super( frame, new XmlExercise( "exercises/level3B.xml" ) {
            public boolean evaluate( Answer choice ) {
                double dist = model.getStarShip().getPov().distance( targetPt );
                return Math.abs( dist ) <= 50;
            }
        } );
        model.getStarShip().setPov( starPt.getX() - 1, starPt.getY() + .5, Math.toRadians( 15 ) );
        model.getStarField().reset();
        for( int i = 0; i < starField.length; i++ ) {
            Star star = starField[i];
            model.getStarField().addStar( star );
        }
        model.setStarField( model.getStarField() );

        // Diable the coordinate graphics
        cockpitModule.setStarshipCordinateGraphicEnabled( false );
        starMapModule.setStarshipCordinateGraphicEnabled( false );
    }
}
