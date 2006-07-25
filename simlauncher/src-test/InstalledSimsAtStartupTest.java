/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.simlauncher.model.Catalog;
import edu.colorado.phet.simlauncher.model.Simulation;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;

import java.util.List;

/**
 * InstalledSimsAtStartupTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimsAtStartupTest {

    public static void main( String[] args ) throws SimResourceException {
        List sims = Catalog.instance().getInstalledSimulations();
        for( int i = 0; i < sims.size(); i++ ) {
            Simulation simulation = (Simulation)sims.get( i );
            System.out.println( "simulation = " + simulation );
        }
    }
}
