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

import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;

import java.util.Random;

/**
 * Extends Atom class from the Laser simulation in that it knows how to collide with
 * an electron
 */
public class DischargeLampAtom extends Atom {

    /**
     * @param model
     * @param numStates
     */
    public DischargeLampAtom( LaserModel model, int numStates ) {
        super( model, numStates );
    }

    /**
     * If the energy of the electron is higher than the energy state of the atom, the atom absorbs some of the
     * electron's energy and goes to a state higher in energy by the amount it absorbs. Exactly how much energy
     * it absorbs is random.
     *
     * @param electron
     */
    public void collideWithElectron( Electron electron ) {
        AtomicState[] states = getStates();
        AtomicState currState = getCurrState();

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
            if( states[highestPossibleNewStateIdx].getEnergyLevel() - currState.getEnergyLevel() > electron.getEnergy() ) {
                highestPossibleNewStateIdx--;
                break;
            }
        }

        // Pick a random state between that of the next higher energy state and the highest energy state
        // we found in the preceding block
        Random random = new Random( System.currentTimeMillis() );
        int newStateIdx = random.nextInt( highestPossibleNewStateIdx - currStateIdx ) + 1;
        AtomicState newState = states[newStateIdx];

        // Put the atom in the randomly picked state, and reduce the energy of the electron by the difference
        // in energy between the new state and the old state
        double energyDiff = newState.getEnergyLevel() - currState.getEnergyLevel();
        electron.setEnergy( electron.getEnergy() - energyDiff );
        this.setCurrState( newState );

    }
}
