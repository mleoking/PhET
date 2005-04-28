/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent;

import edu.colorado.phet.common.model.clock.AbstractClock;

/**
 * MultipleAtomModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MultipleAtomModule extends DischargeLampModule {

    /**
     * Constructor
     *
     * @param clock
     */
    protected MultipleAtomModule( String name, AbstractClock clock, int numAtoms, int numEnergyLevels, double maxAtomSpeed ) {
        super( name, clock, numEnergyLevels );
        addAtoms( getTube(), numAtoms, numEnergyLevels, maxAtomSpeed );
    }
}
