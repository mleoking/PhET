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

import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.common.model.BaseModel;

/**
 * Extends Atom class from the Laser simulation in that it knows how to collide with
 * an electron
 */
public class DischargeLampAtom extends Atom {

    /**
     *
     * @param model
     * @param numStates
     */
    public DischargeLampAtom( BaseModel model, int numStates ) {
        super( model, numStates );
    }

    /**
     *
     * @param electron
     */
    public void collideWithElectron( Electron electron ) {
        AtomicState[] states = getStates();
        AtomicState currState = getCurrState();
        for( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            if( state == currState ) {
                for( int j = 0; j < states.length; j++ ) {
                    AtomicState atomicState = states[j];
                    if( atomicState.getEnergyLevel() - currState.getEnergyLevel() == electron.getEnergy() ) {
                        this.setCurrState( atomicState );
                        return;
                    }
                }
            }
        }
    }
}
