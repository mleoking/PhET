/**
 * Class: Level1
 * Package: edu.colorado.phet.distanceladder.levels
 * Author: Another Guy
 * Date: Apr 16, 2004
 */
package edu.colorado.phet.distanceladder.levels;

import edu.colorado.phet.distanceladder.exercise.Answer;
import edu.colorado.phet.distanceladder.exercise.Exercise;
import edu.colorado.phet.distanceladder.exercise.XmlExercise;
import edu.colorado.phet.distanceladder.model.NormalStar;
import edu.colorado.phet.distanceladder.model.Star;
import edu.colorado.phet.distanceladder.model.UniverseModel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class Level3 extends Exercise {

    private static Point2D.Double starPt = new Point2D.Double( -1600, -1600 );
    private static Point2D.Double targetPt = new Point2D.Double( 1600, 1800 );

    private Star[] starField = new Star[]{
        new NormalStar( Color.green, 1E6, starPt, -15 ),
        new NormalStar( Color.yellow, 1E6, new Point2D.Double( -750, -100 ), -25 ),
        new NormalStar( Color.yellow, 1E6, new Point2D.Double( 800, 300 ), 35 ),
        new NormalStar( Color.red, 1E6, targetPt, 20 ),
    };

    public Level3( JFrame frame, final UniverseModel model ) {
        super( frame, new XmlExercise( "exercises/level3.xml" ) {
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
    }
}
