/**
 * Class: Level1
 * Package: edu.colorado.phet.distanceladder.levels
 * Author: Another Guy
 * Date: Apr 16, 2004
 */
package edu.colorado.phet.distanceladder.levels;

import edu.colorado.phet.distanceladder.exercise.Exercise;
import edu.colorado.phet.distanceladder.exercise.ExerciseModel;
import edu.colorado.phet.distanceladder.exercise.Answer;
import edu.colorado.phet.distanceladder.model.UniverseModel;
import edu.colorado.phet.distanceladder.model.StarField;
import edu.colorado.phet.distanceladder.model.NormalStar;
import edu.colorado.phet.distanceladder.model.Star;
import edu.colorado.phet.distanceladder.Config;

import javax.swing.*;
import java.util.Random;
import java.awt.geom.Point2D;
import java.awt.*;

public class Level2 extends Exercise {

    private Color[] colors = new Color[]{Color.green, Color.magenta, Color.orange,
                                         Color.white, Color.yellow};

    public Level2( JFrame frame, UniverseModel model ) {
        super( frame, new Level1Model() );
        StarField starField = model.getStarField();
        starField.reset();

        Star star = null;
        Random random = new Random();
        for( int i = 0; i < 200; i++ ) {
            double x = random.nextDouble() * Config.universeWidth - Config.universeWidth * 0.5;
            double y = random.nextDouble() * Config.universeWidth - Config.universeWidth * 0.5;
            int colorIdx = random.nextInt( colors.length );
            star = new NormalStar( colors[colorIdx], 50, new Point2D.Double( x, y ), random.nextDouble() * 500 - 250 );
            starField.addStar( star );
        }

        star = new NormalStar( Color.green, 1E6, new Point2D.Double( 100, -50 ), -45 );
        starField.addStar( star );
        star = new NormalStar( Color.magenta, 1E6, new Point2D.Double( 200, -50 ), -35 );
        starField.addStar( star );


        model.setStarField( starField );
    }


    private static class Level1Model extends ExerciseModel {
        static Answer correctAnswer = new Answer( "b", "The blue star" );
        static Answer[] answers = new Answer[]{
            new Answer( "a", "The red star" ),
            correctAnswer,
            new Answer( "c", "The gree star" )
        };

        Level1Model() {
            super( "Which star is closest?", answers, correctAnswer );
        }
    }
}
