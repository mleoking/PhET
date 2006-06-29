/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.simlauncher.JavaSimulation;
import edu.colorado.phet.simlauncher.Catalog;
import edu.colorado.phet.simlauncher.resources.SimResourceException;

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
            JavaSimulation simulation = (JavaSimulation)sims.get( i );
            System.out.println( "simulation = " + simulation );
        }
    }
}
