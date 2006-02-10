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
import edu.colorado.phet.quantum.model.GroundState;

/**
 * DefaultEnergyEmissionStrategy
 * <p/>
 * Always sets the atom to the ground state
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DefaultEnergyEmissionStrategy implements EnergyEmissionStrategy {

    public AtomicState emitEnergy( Atom atom ) {
        AtomicState newState = null;
        AtomicState[] states = atom.getStates();
        for( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            if( state instanceof GroundState ) {
                newState = state;
            }
        }
        return newState;
    }
}
