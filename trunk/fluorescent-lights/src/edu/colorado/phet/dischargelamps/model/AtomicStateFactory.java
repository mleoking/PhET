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

import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.GroundState;

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
//        double minVisibleEnergy = Photon.wavelengthToEnergy( Photon.DEEP_RED );
//        double maxVisibleEnergy = Photon.wavelengthToEnergy( Photon.BLUE );

        // Determine the spacing of energy levels
        double dE = states.length > 2 ? ( maxVisibleEnergy - minVisibleEnergy ) / ( states.length - 2 ) :
             ( maxVisibleEnergy - minVisibleEnergy ) * 2 / 3 ;
//        dE = states.length > 2 ? ( maxVisibleEnergy - minVisibleEnergy ) / ( states.length - 2 ) : 0;

        states[0] = new GroundState();
        states[0].setEnergyLevel( minVisibleEnergy );
        for( int i = 1; i < states.length; i++ ) {
            states[i] = new AtomicState();
            states[i].setMeanLifetime( DischargeLampAtom.DEFAULT_STATE_LIFETIME );
            states[i].setEnergyLevel( minVisibleEnergy + ( i ) * dE );
//            states[i].setEnergyLevel( minVisibleEnergy + ( i - 1 ) * dE );
            states[i].setNextLowerEnergyState( states[i - 1] );
            states[i - 1].setNextHigherEnergyState( states[i] );
        }
        states[states.length - 1].setNextHigherEnergyState( AtomicState.MaxEnergyState.instance() );
        return states;
    }
}
