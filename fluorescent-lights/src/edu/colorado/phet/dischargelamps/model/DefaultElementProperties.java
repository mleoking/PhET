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

/**
 * HydrogenProperties
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DefaultElementProperties extends ElementProperties {
    private static double[] energyLevels = {
        -13.6,
        -0.378
    };

    public DefaultElementProperties( int numEnergyLevels ) {
        super( "Configurable", energyLevels );
        AtomicState[] states = new AtomicStateFactory().createAtomicStates( numEnergyLevels );
        double[] newEnergyLevels = new double[numEnergyLevels];
        for( int i = 0; i < newEnergyLevels.length; i++ ) {
            newEnergyLevels[i] = states[i].getEnergyLevel();
        }
        setEnergyLevels( newEnergyLevels );
    }
}

