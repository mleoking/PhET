/* Copyright 2008, University of Colorado */

package edu.colorado.phet.buildtools.scripts;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.util.SvnUtils;

/**
 * AddSVNIgnoreToDeployDirectories is a utility for adding the svn:ignore property to all deploy directories.
 * The PhET build process generates JAR, JNLP, SWF, JPG, GIF, PNG, HEADER files, and puts them in the deploy directories.
 * We do not want to check in these files, so we need to tell SVN to ignore them.
 * This is done via the svn:ignore property, and that property is set using the "svn propset" command.
 * See "svn help propset" for details.
 */
public class SetSVNIgnoreToDeployDirectories {

    public static void main( String[] args ) throws IOException, InterruptedException {

        // trunk must be specified on the command line
        if ( args.length != 1 ) {
            System.out.println( "usage: SetSVNIgnoreToDeployDirectories trunk" );
            System.exit( 1 );
        }

        // Verify that trunk exists
        File trunk = new File( args[0] );
        if ( !trunk.exists() || !trunk.isDirectory() ) {
            System.out.println( trunk.getAbsolutePath() + " does not exist or is not a directory" );
        }

        PhetProject[] projects = PhetProject.getAllProjects( trunk );
        for ( int k = 0; k < projects.length; k++ ) {
            File dir = projects[k].getDeployDir();
            String[] ignorePatterns = new String[] { "*" };
            SvnUtils.setIgnorePatternsOnDir( dir, ignorePatterns );
            System.out.println( "Updated properties on: " + dir.getAbsolutePath() );
        }
    }

}
