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

import java.util.Random;

/**
 * DefaultEnergyEmissionStrategy
 * <p/>
 * Fifty percent of the time, the atom goes to the ground state. The other half of the
 * time, it goes to a randomly selected state in between.
 * <p/>
 * This class assumes an atom's states are sorted in order of ascending energy.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FiftyPercentEnergyEmissionStrategy implements EnergyEmissionStrategy {
    private static Random random = new Random();

    public AtomicState emitEnergy( Atom atom ) {
        AtomicState[] states = atom.getStates();
        int nextStateIdx = 0;

        // find index of atom's current state
        int currStateIdx = 0;
        for( int i = 0; i < states.length; i++ ) {
            if( states[i] == atom.getCurrState() ) {
                currStateIdx = i;
                break;
            }
        }
        if( random.nextBoolean() || currStateIdx < 2 ) {
            nextStateIdx = 0;
        }
        else {
            do {
                nextStateIdx = random.nextInt( currStateIdx );
            } while( nextStateIdx == 0 );
        }
        return states[nextStateIdx];
    }
}
