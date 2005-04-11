/**
 * Class: ExerciseView
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 10:01:58 PM
 */
package edu.colorado.phet.distanceladder.exercise;

import javax.swing.*;

public class ExerciseView {
    private ExerciseModel exercise;

    public ExerciseView( ExerciseModel exercise ) {
        this.exercise = exercise;
    }

    public boolean doIt() {
        Object choice = JOptionPane.showInputDialog( null,
                                     exercise.getQuestion(),
                                     "So tell me...",
                                     JOptionPane.QUESTION_MESSAGE,
                                     null,
                                     exercise.getChoices(),
                                     "a" );
        return ( choice == exercise.getCorrectAnswer() );
    }
}
