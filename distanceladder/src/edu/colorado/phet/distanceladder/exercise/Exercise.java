/**
 * Class: Exercise
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Apr 6, 2004
 * Time: 3:18:16 PM
 */
package edu.colorado.phet.distanceladder.exercise;

import edu.colorado.phet.coreadditions.fsm.FsmState;
import edu.colorado.phet.coreadditions.fsm.FsmTransition;
import edu.colorado.phet.coreadditions.fsm.FsmEvent;

public class Exercise extends FsmState {
    private ExerciseModel model;
    private ExerciseView view;

    public Exercise( ExerciseModel model ) {
        this.model = model;
        view = new ExerciseView( model );
//
//        for( int i = 0; i < model.getChoices().length; i++ ){
//            FsmTransition transition = new FsmTransition( new Choice( model.getChoices()[i]) );
//        }
    }
}
