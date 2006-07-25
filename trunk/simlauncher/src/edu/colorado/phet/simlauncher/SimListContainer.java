/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import edu.colorado.phet.simlauncher.model.SimContainer;
import edu.colorado.phet.simlauncher.model.Simulation;

import java.util.List;

/**
 * SimListContainer
 * <p/>
 * A utility class that will act as a SimContainer wrapped around a list of Simulations
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

public class SimListContainer implements SimContainer {
    private final List sims;

    public SimListContainer( List sims ) {
        this.sims = sims;
    }

    public Simulation getSimulation() {
        if( sims.size() > 0 ) {
            return (Simulation)sims.get( 0 );
        }
        else {
            return null;
        }
    }

    public Simulation[] getSimulations() {
        Simulation[] array = (Simulation[])sims.toArray( new Simulation[sims.size()] );
        return array;
    }
}
