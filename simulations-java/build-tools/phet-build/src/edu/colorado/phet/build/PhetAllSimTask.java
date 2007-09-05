package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Sam Reid
 * May 14, 2007, 5:47:07 PM
 */
public class PhetAllSimTask extends AbstractPhetTask {
    protected String[] getSimNames() {
        return getSimNames( new File( getProject().getBaseDir(), "simulations" ));
    }

    public static String[] getSimNames(File simsroot){
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

    private static boolean isSimulation( File simulation ) {
        return simulation.isDirectory() && !simulation.getName().equalsIgnoreCase( "all-sims" ) && !simulation.getName().equalsIgnoreCase( ".svn" );
    }

    protected List getAllPhetProjects() {
        List phetProjects = new ArrayList();

        String[] sims = getSimNames();

        for( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];

            File projectDir = PhetBuildUtils.resolveProject( getProject().getBaseDir(), sim );

            try {
                PhetProject phetProject = new PhetProject( projectDir, sim );

                phetProjects.add(phetProject);
            }
            catch (IOException e) {
                throw new BuildException(e);
            }
        }

        return phetProjects;
    }
}
