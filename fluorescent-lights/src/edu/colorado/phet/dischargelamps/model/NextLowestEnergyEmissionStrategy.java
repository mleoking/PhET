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

import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;

/**
 * NextLowestEnergyEmissionStrategy
 * <p/>
 * Always sets the atom to the next lower state down from its current state
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class NextLowestEnergyEmissionStrategy implements EnergyEmissionStrategy {

    public AtomicState emitEnergy( Atom atom ) {
        AtomicState newState = null;
        AtomicState[] states = atom.getStates();
        // Find the current state
        int currStateIdx = 0;
        for( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            if( state.equals( atom.getCurrState() ) ) {
                currStateIdx = i;
            }
        }
        return states[Math.max( currStateIdx - 1, 0 )];
    }
}
