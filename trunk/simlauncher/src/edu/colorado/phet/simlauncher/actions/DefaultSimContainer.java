/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.SimContainer;
import edu.colorado.phet.simlauncher.Simulation;

/**
 * DefaultSimContainer
 * <p>
 * A simple SimContainer that is provided primarily for Actions that can be
 * costructed with either a Simulation or a SimContainer
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class DefaultSimContainer implements SimContainer {
    private Simulation simulation;

    public DefaultSimContainer( Simulation simulation ) {
        this.simulation = simulation;
    }

    public Simulation getSimulation() {
        return simulation;
    }
}
