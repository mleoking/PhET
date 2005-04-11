/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.flourescent.model;

import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.util.EventChannel;

import java.util.EventListener;
import java.util.EventObject;

/**
 *
 */
public abstract class Electrode extends Particle {
    private double potential;

    public double getPotential() {
        return potential;
    }

    public void setPotential( double potential ) {
        this.potential = potential;
        stateChangeListenerProxy.stateChanged( new StateChangeEvent( this ) );
    }

    //-----------------------------------------------------------------
    // Event and listener definitions
    //-----------------------------------------------------------------
    private EventChannel stateChangeEventChannel = new EventChannel( StateChangeListener.class );
    private StateChangeListener stateChangeListenerProxy = (StateChangeListener)stateChangeEventChannel.getListenerProxy();

    public void addStateChangeListener( StateChangeListener listener ) {
        stateChangeEventChannel.addListener( listener );
    }

    public void removeStateChangeListener( StateChangeListener listener ) {
        stateChangeEventChannel.removeListener( listener );
    }

    public class StateChangeEvent extends EventObject {
        public StateChangeEvent( Object source ) {
            super( source );
        }

        public Electrode getElectrode() {
            return (Electrode)getSource();
        }
    }

    public interface StateChangeListener extends EventListener {
        void stateChanged( StateChangeEvent event );
    }
}
