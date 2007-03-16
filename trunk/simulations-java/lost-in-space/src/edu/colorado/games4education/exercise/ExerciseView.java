/**
 * Class: ExerciseView
 * Class: edu.colorado.games4education.exercise
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 10:01:58 PM
 */
package edu.colorado.games4education.exercise;

import javax.swing.*;

public class ExerciseView {
    private Exercise exercise;

    public ExerciseView( Exercise exercise ) {
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
