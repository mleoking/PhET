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
import edu.colorado.phet.quantum.model.Electron;

import java.util.Random;

/**
 * FiftyPercentAbsorptionStrategy
 * <p/>
 * Pick a state between that of the next higher energy state and the highest energy state
 * we found in the preceding block. The highest state has a 50% chance of being picked, and
 * all other states have equal probablity within the remaining 50%.
 * <p/>
 * Assumes that the atom's array of states is sorted in ascending order of energy.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FiftyPercentAbsorptionStrategy extends EnergyAbsorptionStrategy {

    private static Random random = new Random();

    /**
     * If the electron's energy is greater than the difference between the atom's current energy and one of
     * its higher energy states, the atom absorbs some of the electron's energy and goes to a state higher
     * in energy by the amount it absorbs. The state chosen is determined according the the description in
     * the class header.
     *
     * @param electron
     */
    public void collideWithElectron( Atom atom, Electron electron ) {
        AtomicState[] states = atom.getStates();
        AtomicState currState = atom.getCurrState();
        double electronEnergy = getElectronEnergyAtCollision( (DischargeLampAtom)atom, electron );

        // Find the index of the current state
        int currStateIdx = 0;
        for( ; currStateIdx < states.length; currStateIdx++ ) {
            if( states[currStateIdx] == currState ) {
                break;
            }
        }

        // Find the index of the highest energy state whose energy is not higher than that of the current state
        // by more than the energy of the electron
        int highestPossibleNewStateIdx = currStateIdx + 1;
        for( ; highestPossibleNewStateIdx < states.length; highestPossibleNewStateIdx++ ) {
            if( states[highestPossibleNewStateIdx].getEnergyLevel() - currState.getEnergyLevel() > electronEnergy ) {
                break;
            }
        }
        highestPossibleNewStateIdx--;

        // Pick a state between that of the next higher energy state and the highest energy state
        // we found in the preceding block. The highest state has a 50% chance of being picked, and
        // all other states have equal probablity within the remaining 50%
        if( highestPossibleNewStateIdx > currStateIdx ) {
            int newStateIdx = 0;
            if( random.nextBoolean() || highestPossibleNewStateIdx == currStateIdx + 1 ) {
                newStateIdx = highestPossibleNewStateIdx;
            }
            else {
                int rand = random.nextInt( highestPossibleNewStateIdx - currStateIdx - 1 ) + 1;
                newStateIdx = rand + currStateIdx;
            }
            AtomicState newState = states[newStateIdx];

            // Put the atom in the randomly picked state, and reduce the energy of the electron by the difference
            // in energy between the new state and the old state
            double energyDiff = newState.getEnergyLevel() - currState.getEnergyLevel();
            atom.setCurrState( newState );
            electron.setEnergy( electronEnergy - energyDiff );
        }
    }
}
