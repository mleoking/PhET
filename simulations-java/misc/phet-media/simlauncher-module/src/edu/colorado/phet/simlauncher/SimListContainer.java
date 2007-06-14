/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/SimListContainer.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.2 $
 * Date modified : $Date: 2006/07/25 18:00:18 $
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
 * @version $Revision: 1.2 $
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
