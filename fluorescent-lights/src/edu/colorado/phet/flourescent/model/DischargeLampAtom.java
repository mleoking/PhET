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
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.flourescent.FluorescentLightsConfig;

import java.util.Random;

/**
 * Extends Atom class from the Laser simulation in that it knows how to collide with
 * an electron
 */
public class DischargeLampAtom extends Atom {
    private static final double DEFAULT_STATE_LIFETIME = FluorescentLightsConfig.DT * 6;
    private Random random = new Random( System.currentTimeMillis() );

    /**
     * @param model
     * @param numStates
     */
    public DischargeLampAtom( LaserModel model, int numStates ) {
        super( model, numStates, true );

        AtomicState[] states = new AtomicState[numStates];
        double de = ( Photon.wavelengthToEnergy( Photon.BLUE ) - AtomicState.minEnergy ) / ( states.length - 1 );
        states[0] = new GroundState();
        for( int i = 1; i < states.length; i++ ) {
            states[i] = new AtomicState();
            states[i].setMeanLifetime( DEFAULT_STATE_LIFETIME );
            states[i].setEnergyLevel( states[i - 1].getEnergyLevel() + de );
            states[i].setNextLowerEnergyState( states[i - 1] );
            states[i - 1].setNextHigherEnergyState( states[i] );
        }
        states[states.length - 1].setNextHigherEnergyState( AtomicState.MaxEnergyState.instance() );
        setStates( states );
        setCurrState( states[0] );
    }

    /**
     * If the electron's energy is greater than the difference between the atom's current energy and one of
     * its higher energy states, the atom absorbs some of the electron's energy and goes to a state higher
     * in energy by the amount it absorbs. Exactly how much energy it absorbs is random.
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
                break;
            }
        }
        highestPossibleNewStateIdx--;

        // Pick a random state between that of the next higher energy state and the highest energy state
        // we found in the preceding block
        if( highestPossibleNewStateIdx > currStateIdx ) {
            int rand = random.nextInt( highestPossibleNewStateIdx - currStateIdx ) + 1;
            int newStateIdx = rand + currStateIdx;
            AtomicState newState = states[newStateIdx];

            // Put the atom in the randomly picked state, and reduce the energy of the electron by the difference
            // in energy between the new state and the old state
            double energyDiff = newState.getEnergyLevel() - currState.getEnergyLevel();
            this.setCurrState( newState );
            electron.setEnergy( electron.getEnergy() - energyDiff );
        }
    }

    /**
     * Returns the state the atom will be in after it emits a photon. By default, this is the
     * ground state
     * @return
     */
    public AtomicState getEnergyStateAfterEmission() {
        return getStates()[0];
    }
}
