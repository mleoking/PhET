package edu.colorado.phet.buildtools;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;

/**
 * Ant task so the the build-all can be run from Ant
 * See #3326
 */
public class BuildAll extends Task {
    public void execute() throws BuildException {
        super.execute();

        String trunk = System.getProperty( "trunk" );
        if ( trunk == null ) {
            throw new BuildException( "Trunk must be passed as a system property, like -Dtrunk=/path/to/trunk" );
        }
        final File trunkFile = new File( trunk );
        if ( !trunkFile.exists() ) {
            throw new BuildException( "No such trunk: " + trunkFile.getAbsolutePath() );
        }
        PhetProject[] projects = PhetProject.getAllSimulationProjects( trunkFile );
        for ( PhetProject phetProject : projects ) {
            if ( phetProject instanceof JavaSimulationProject ) {
                try {
                    boolean success = phetProject.build();
                    if ( !success ) {
                        throw new BuildException( "Build failure: " + phetProject.getName() );
                    }
                }
                catch ( Exception e ) {
                    throw new BuildException( "build exception: " + phetProject.getName(), e );
                }
            }
        }
    }
}