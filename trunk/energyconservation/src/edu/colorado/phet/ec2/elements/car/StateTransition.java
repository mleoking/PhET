/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.car;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 27, 2003
 * Time: 12:41:11 AM
 * Copyright (c) Jul 27, 2003 by Sam Reid
 */
public class StateTransition {
    StateChangeTest scs;
    CarMode targetState;
    ArrayList listeners = new ArrayList();

    public StateTransition( StateChangeTest scs, CarMode targetState ) {
        this.scs = scs;
        this.targetState = targetState;
    }

    public boolean testAndApply( Car car, double dt ) {
        if( scs.shouldChangeState() ) {
            targetState.initialize( car, dt );
            car.setMode( targetState );
            for( int i = 0; i < listeners.size(); i++ ) {
                StateTransitionListener o = (StateTransitionListener)listeners.get( i );
                o.transitionChanged( targetState );
            }
            return true;
        }
        return false;
    }

    public void addStateTransitionListener( StateTransitionListener stl ) {
        listeners.add( stl );
    }

    public void removeStateTransitionListener( StateTransitionListener listener ) {
        listeners.remove( listener );
    }
}
