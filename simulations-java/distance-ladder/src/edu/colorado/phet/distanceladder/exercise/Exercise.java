/**
 * Class: Exercise
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Apr 6, 2004
 * Time: 3:18:16 PM
 */
package edu.colorado.phet.distanceladder.exercise;

import edu.colorado.phet.coreadditions.fsm.FsmState;

import javax.swing.*;

public class Exercise extends FsmState {
    private ExerciseModel model;
    private ExerciseView view;

    public Exercise( JFrame frame, ExerciseModel model ) {
        this.model = model;
        view = new ExerciseView( frame, model );
    }

    public boolean doIt() {
        return view.doIt();
    }
}
