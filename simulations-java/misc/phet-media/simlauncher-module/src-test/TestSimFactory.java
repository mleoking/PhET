/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src-test/TestSimFactory.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.4 $
 * Date modified : $Date: 2006/07/25 18:00:18 $
 */

import edu.colorado.phet.simlauncher.PhetWebPage;
import edu.colorado.phet.simlauncher.model.SimFactory;
import edu.colorado.phet.simlauncher.model.Simulation;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;

import java.io.IOException;
import java.util.List;

/**
 * TestSimFactory
 * <p>
 * gets all the simulations from the Top Simulations page and installs them
 *
 * @author Ron LeMaster
 * @version $Revision: 1.4 $
 */
public class TestSimFactory {
    public static void main( String[] args ) throws IOException, SimResourceException {
        List simulations = new SimFactory().getSimulations( new PhetWebPage( "http://www.colorado.edu/physics/phet/web-pages/simulation-pages/top-simulations.htm" ) );
        for( int i = 0; i < simulations.size(); i++ ) {
            Simulation simulation = (Simulation)simulations.get( i );
            simulation.install();
        }
    }
}
