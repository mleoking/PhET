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

public class Level2A extends Exercise {

    private static Point2D.Double starLocation = new Point2D.Double( 650, -80 );

    private Star[] starField = new Star[]{
        new NormalStar( Color.green, 1E6, starLocation, -20 ),
    };

    public Level2A( JFrame frame, final UniverseModel model ) {
        super( frame, new XmlExercise( "exercises/level2A.xml" ) {
            public boolean evaluate( Answer choice ) {
                double dist = model.getStarShip().getPov().distance( starLocation );
                return Math.abs( dist ) <= 50;
            }
        } );
//        super( frame, new XmlExercise( "exercises/level2A.xml" ) );
        model.getStarField().reset();
        for( int i = 0; i < starField.length; i++ ) {
            Star star = starField[i];
            model.getStarField().addStar( star );
        }
        model.setStarField( model.getStarField() );
    }
}
