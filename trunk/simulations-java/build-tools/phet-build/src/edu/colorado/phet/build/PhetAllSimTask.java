package edu.colorado.phet.build;

import java.io.File;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 14, 2007, 5:47:07 PM
 */
public class PhetAllSimTask extends AbstractPhetTask {
    protected String[] getSimNames() {
        File simsroot = new File( getProject().getBaseDir(), "simulations" );
        File[] simulations = simsroot.listFiles();
        ArrayList sims = new ArrayList();
        for( int i = 0; i < simulations.length; i++ ) {
            File simulation = simulations[i];
            if( isSimulation( simulation ) ) {
                sims.add( simulation.getName() );
            }
        }
        return (String[])sims.toArray( new String[0] );
    }

    private boolean isSimulation( File simulation ) {
        return simulation.isDirectory() && !simulation.getName().equalsIgnoreCase( "all-sims" ) && !simulation.getName().equalsIgnoreCase( ".svn" );
    }
}
