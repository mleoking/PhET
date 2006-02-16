/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum;

import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.quantum.model.AtomicState;
import edu.colorado.phet.quantum.model.GroundState;

/**
 * AtomicStateFactory
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AtomicStateFactory {

    public AtomicState[] createAtomicStates( int numEnergyLevels ) {
        AtomicState[] states = new AtomicState[numEnergyLevels];
        double minVisibleEnergy = -13.6;
        double maxVisibleEnergy = -0.3;

        // Determine the spacing of energy levels
        double dE = ( maxVisibleEnergy - minVisibleEnergy ) / ( states.length - 1 );

        states[0] = new GroundState();
        states[0].setEnergyLevel( minVisibleEnergy );
        states[1] = new AtomicState();
        states[1].setEnergyLevel( minVisibleEnergy + 4 );
        states[0].setNextHigherEnergyState( states[1] );
        states[1].setNextLowerEnergyState( states[0] );
        states[1].setMeanLifetime( DischargeLampAtom.DEFAULT_STATE_LIFETIME );
        dE = maxVisibleEnergy - states[1].getEnergyLevel() / 4;
        for( int i = 2; i < states.length && i < DischargeLampModel.MAX_STATES; i++ ) {
            states[i] = new AtomicState();
            states[i].setMeanLifetime( DischargeLampAtom.DEFAULT_STATE_LIFETIME );
            states[i].setEnergyLevel( states[i - 1].getEnergyLevel() + dE );
            states[i].setNextLowerEnergyState( states[i - 1] );
            states[i - 1].setNextHigherEnergyState( states[i] );
        }
        states[states.length - 1].setNextHigherEnergyState( AtomicState.MaxEnergyState.instance() );
        return states;
    }

    /**
     * Creates or removes states from a specified array of states. If states are added, they are
     * evenly spaced between the highest previous state and the maximum energy allow by the model.
     *
     * @param numEnergyLevels
     * @param existingStates
     * @return
     */
    public AtomicState[] createAtomicStates( int numEnergyLevels, AtomicState[] existingStates ) {
        double maxVisibleEnergy = -0.3;

        if( existingStates.length < 2 ) {
            return createAtomicStates( numEnergyLevels );
        }
        else {
            // If the requested number of states is less than the number of existing states...
            AtomicState[] newStates = new AtomicState[numEnergyLevels];
            double dE = ( maxVisibleEnergy - existingStates[existingStates.length - 1].getEnergyLevel() )
                        / ( DischargeLampModel.MAX_STATES - existingStates.length );
            for( int i = 0; i < newStates.length; i++ ) {
                if( i < existingStates.length ) {
                    newStates[i] = existingStates[i];
                }
                else {
                    newStates[i] = new AtomicState();
                    newStates[i].setMeanLifetime( DischargeLampAtom.DEFAULT_STATE_LIFETIME );
                    newStates[i].setEnergyLevel( newStates[i - 1].getEnergyLevel() + dE );
                    newStates[i].setNextLowerEnergyState( newStates[i - 1] );
                    newStates[i - 1].setNextHigherEnergyState( newStates[i] );
                }
            }
            newStates[newStates.length - 1].setNextHigherEnergyState( AtomicState.MaxEnergyState.instance() );
            return newStates;
        }
    }
}
