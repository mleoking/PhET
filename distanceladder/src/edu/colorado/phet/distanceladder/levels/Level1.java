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

import javax.swing.*;

public class Level1 extends Exercise {

    public Level1() {
        super( new Level1Model() );        
    }



    private static class Level1Model extends ExerciseModel {
        static Answer correctAnswer = new Answer( "b", "The blue star" );
        static Answer[] answers = new Answer[] {
            new Answer( "a", "The red star" ),
            correctAnswer,
            new Answer( "c", "The gree star" )
        };

        Level1Model() {
            super( "Which star is closest?", answers, correctAnswer );
        }
    }
}
