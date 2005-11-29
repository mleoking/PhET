/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.controller.LaserConfig;

/**
 * LaserElementProperties
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class LaserElementProperties extends ElementProperties {

    protected LaserElementProperties( String name, double[] energyLevels,
                                      EnergyEmissionStrategy energyEmissionStrategy,
                                      double meanStateLifetime ) {
        super( name, energyLevels, energyEmissionStrategy, meanStateLifetime );

        // Set the mean lifetimes of the states
        AtomicState[] states = getStates();
        for( int i = 1; i < states.length; i++ ) {
            AtomicState state = states[i];
            state.setMeanLifetime( LaserConfig.MAXIMUM_STATE_LIFETIME / 2 );
        }

    }

    public AtomicState getGroundState() {
        return getStates()[0];
    }

    public AtomicState getMiddleEnergyState() {
        return getStates()[1];
    }

    abstract public AtomicState getHighEnergyState();
}
