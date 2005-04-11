/**
 * Class: FsmTransition
 * Class: edu.colorado.phet.distanceladder.exercise
 * User: Ron LeMaster
 * Date: Apr 6, 2004
 * Time: 2:56:19 PM
 */
package edu.colorado.phet.coreadditions.fsm;

import edu.colorado.phet.coreadditions.fsm.FsmEvent;
import edu.colorado.phet.coreadditions.fsm.FsmState;

public class FsmTransition {
    private FsmEvent event;
    private String nextStateId;
    private Runnable onTransition = new Runnable() {
        public void run() {
        }
    };

    public FsmTransition( FsmEvent event, String nextStateId, Runnable onTransition ) {
        this.event = event;
        this.nextStateId = nextStateId;
        if( onTransition != null ) {
            this.onTransition = onTransition;
        }
    }

    public FsmEvent getEvent() {
        return event;
    }

    public String getNextStateId() {
        return nextStateId;
    }
}
