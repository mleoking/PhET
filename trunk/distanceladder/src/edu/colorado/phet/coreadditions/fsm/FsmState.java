/**
 * Class: FsmState
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Apr 6, 2004
 * Time: 2:49:09 PM
 */
package edu.colorado.phet.coreadditions.fsm;

import java.util.HashMap;

public class FsmState {
    private FiniteStateMachine fsm;
    private String id;
    private HashMap transitions = new HashMap();
    private Runnable onEntry = new Runnable() {
        public void run() {
        }
    };
    private Runnable onExit = new Runnable() {
        public void run() {
        }
    };

    public FsmState() {
    }

    public FsmState( FiniteStateMachine fsm, HashMap transitions, Runnable onEntry, Runnable onExit ) {
        this.fsm = fsm;
        this.transitions = transitions;
        if( onEntry != null ) {
            this.onEntry = onEntry;
        }
        if( onExit != null ) {
            this.onExit = onExit;
        }
    }

    public HashMap getTransitions() {
        return transitions;
    }

    public void setTransitions( HashMap transitions ) {
        this.transitions = transitions;
    }

    public void setOnEntry( Runnable onEntry ) {
        this.onEntry = onEntry;
    }

    public void setOnExit( Runnable onExit ) {
        this.onExit = onExit;
    }

    public FsmState respondToEvent( FsmEvent event ) {
        FsmState nextState = this;
        FsmTransition transition = (FsmTransition)transitions.get( event );
        if( transition != null ) {
            this.onExit.run();
            nextState = fsm.getState( transition.getNextStateId() );
            nextState.onEntry.run();
        }
        return nextState;
    }

    public String getId() {
        return id;
    }
}
