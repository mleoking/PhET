/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src-test/TestLastLaunchTimestamp.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.5 $
 * Date modified : $Date: 2006/07/25 18:00:18 $
 */

import edu.colorado.phet.simlauncher.model.Catalog;
import edu.colorado.phet.simlauncher.model.Simulation;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;

import java.util.List;

/**
 * TestLastLaunchTimestamp
 *
 * @author Ron LeMaster
 * @version $Revision: 1.5 $
 */
public class TestLastLaunchTimestamp {
    public static void main( String[] args ) throws SimResourceException {
        List sims = Catalog.instance().getAllSimulations();
        for( int i = 0; i < sims.size(); i++ ) {
            Simulation simulation = (Simulation)sims.get( i );
            if( simulation.isInstalled() ) {
                System.out.println( "simulation.getName() = " + simulation.getName() + "  last launched: " +
                                    simulation.getLastLaunchTimestamp() );
            }
        }
    }
}
