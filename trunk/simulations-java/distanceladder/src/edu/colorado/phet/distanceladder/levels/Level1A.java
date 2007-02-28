/**
 * Class: Level1
 * Package: edu.colorado.phet.distanceladder.levels
 * Author: Another Guy
 * Date: Apr 16, 2004
 */
package edu.colorado.phet.distanceladder.levels;

import edu.colorado.phet.distanceladder.Config;
import edu.colorado.phet.distanceladder.exercise.Exercise;
import edu.colorado.phet.distanceladder.exercise.XmlExercise;
import edu.colorado.phet.distanceladder.model.NormalStar;
import edu.colorado.phet.distanceladder.model.Star;
import edu.colorado.phet.distanceladder.model.UniverseModel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class Level1A extends Exercise {

    private Star[] starField = new Star[]{
        new NormalStar( Color.green, Config.maxStarLuminance, new Point2D.Double( 100, -20 ), -45 ),
        new NormalStar( Color.red, Config.maxStarLuminance, new Point2D.Double( 250, 10 ), -5 ),
        new NormalStar( Color.blue, Config.maxStarLuminance, new Point2D.Double( Config.universeWidth / 4, 30 ), 5 ),
        new NormalStar( Color.yellow, Config.maxStarLuminance, new Point2D.Double( Config.universeWidth / 2, -15 ), 0 )
    };

    public Level1A( JFrame frame, UniverseModel model ) {
        super( frame, new XmlExercise( "exercises/level1A.xml" ) );
        model.getStarField().reset();
        for( int i = 0; i < starField.length; i++ ) {
            Star star = starField[i];
            model.getStarField().addStar( star );
        }
        model.setStarField( model.getStarField() );
    }


//    private static class Level1Model extends ExerciseModel {
//        static Answer correctAnswer = new Answer( "b", "The blue star" );
//        static Answer[] answers = new Answer[]{
//            new Answer( "a", "The red star" ),
//            correctAnswer,
//            new Answer( "c", "The gree star" )
//        };
//
//        Level1Model() {
//            super( "Which star is closest?", answers, correctAnswer );
//        }
//    }
}
