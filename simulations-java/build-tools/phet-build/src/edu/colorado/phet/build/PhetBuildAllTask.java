package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;

import java.io.File;

public class PhetBuildAllTask extends AbstractPhetTask {
    public final void execute() throws BuildException {
        System.out.println( "PhetBuildAllTask.execute" );
        File simsroot = new File( getProject().getBaseDir(), "simulations" );
        File[] simulations = simsroot.listFiles();
        for( int i = 0; i < simulations.length; i++ ) {
            File simulation = simulations[i];
            if( isSimulation( simulation ) ) {
                PhetBuildTask phetBuildTask = new PhetBuildTask();
                phetBuildTask.setProject( simulation.getName() );
                runTask( phetBuildTask );
            }
        }

        System.out.println( "files = " + simsroot );
    }

    private boolean isSimulation( File simulation ) {
        return simulation.isDirectory() && !simulation.getName().equalsIgnoreCase( "all-sims" ) && !simulation.getName().equalsIgnoreCase( ".svn" );
    }

}
