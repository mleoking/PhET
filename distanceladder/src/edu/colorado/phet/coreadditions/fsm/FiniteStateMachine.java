/**
 * Class: FiniteStateMachine
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Apr 6, 2004
 * Time: 2:47:16 PM
 */
package edu.colorado.phet.coreadditions.fsm;

import java.util.ArrayList;
import java.util.HashMap;

public class FiniteStateMachine {
    private HashMap states = new HashMap( );
    private FsmState currentState;

    public void addState( FsmState state ) {
        if( states.entrySet().contains( state )) {
            throw new RuntimeException( "Attempt to add state twice to FSM" );
        }
        states.put( state.getId(), state );
    }

    public void setCurrentState( FsmState state ) {
        if( !states.entrySet().contains( state )) {
            throw new RuntimeException( "Invalid state specified as current state" );
        }
        currentState = state;
    }

    public void respondToEvent( FsmEvent event ) {
        setCurrentState( currentState.respondToEvent( event ));
    }

    public FsmState getState( String stateId ) {
        FsmState state = (FsmState)states.get( stateId );
        return null;
    }
}
