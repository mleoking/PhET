/**
 * Class: Script
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Apr 6, 2004
 * Time: 2:42:36 PM
 */
package edu.colorado.phet.distanceladder.exercise;

import edu.colorado.phet.coreadditions.fsm.FiniteStateMachine;

public class Script {
    private FiniteStateMachine fsm;

    public void addExercise( Exercise exercise ) {
        fsm.addState( exercise );
    }
}
