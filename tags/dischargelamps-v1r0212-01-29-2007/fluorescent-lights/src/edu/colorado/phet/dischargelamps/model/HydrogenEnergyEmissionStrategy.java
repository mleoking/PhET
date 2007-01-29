/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import edu.colorado.phet.quantum.model.Atom;
import edu.colorado.phet.quantum.model.AtomicState;
import edu.colorado.phet.quantum.model.EnergyEmissionStrategy;

/**
 * HydrogenEnergyEmissionStrategy
 * <p/>
 * If the atom is in the level just above the ground state, go to the ground state.
 * Otherwise, go to the level just above the ground state.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HydrogenEnergyEmissionStrategy implements EnergyEmissionStrategy {

    public HydrogenEnergyEmissionStrategy() {
        super();
    }

    public AtomicState emitEnergy( Atom atom ) {
        AtomicState newState = null;
        AtomicState[] states = atom.getStates();
        // Find the current state
        int currStateIdx = 0;
        for( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            if( state.equals( atom.getCurrState() ) ) {
                currStateIdx = i;
                break;
            }
        }
        if( currStateIdx > 1 ) {
            newState = states[1];
        }
        else {
            newState = states[0];
        }
        return newState;
    }
}
