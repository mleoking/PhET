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

import edu.colorado.phet.simlauncher.resources.CatalogResource;

import java.util.List;
import java.util.ArrayList;
import java.net.URL;

/**
 * Catalog
 * <p/>
 * A catalog of all the available simulations
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Catalog {

    private CatalogResource catalogResource = new CatalogResource( Configuration.instance().getCatalogUrl(),
                                                           Configuration.instance().getLocalRoot() );

    public List getSimulations() {
        return new SimulationFactory().getSimulations( catalogResource.getLocalFile() );
    }

    public List getInstalledSimulations() {
        List simulations = getSimulations();
        List installedSimulations = new ArrayList();
        for( int i = 0; i < simulations.size(); i++ ) {
            Simulation simulation = (Simulation)simulations.get( i );
            if( simulation.isInstalled() ) {
                installedSimulations.add( simulation );
            }
        }
        return installedSimulations;
    }

}
