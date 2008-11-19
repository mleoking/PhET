/* Copyright 2008, University of Colorado */

package edu.colorado.phet.build.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.colorado.phet.build.PhetProject;

/**
 * AddSVNIgnoreToDeployDirectories is a utility for adding the svn:ignore property to all deploy directories.
 * The PhET build process generates JAR and JNLP files, and puts them in the deploy directories.
 * We do not want to check in these JAR and JNLP files, so we need to tell SVN to ignore them.
 * This is done via the svn:ignore property, and that property is set using the "svn propset" command.
 * See "svn help propset" for details.
 */
public class AddSVNIgnoreToDeployDirectories {

    public static void main( String[] args ) throws IOException {

        // basedir must be on the command line (typically the full pathname of simulations-java)
        if ( args.length != 1 ) {
            System.out.println( "usage: AddSVNIgnoreToDeployDirectories basedir" );
            System.exit( 1 );
        }

        // Verify that the basedir exists
        File baseDir = new File( args[0] );
        if ( !baseDir.exists() || !baseDir.isDirectory() ) {
            System.out.println( baseDir.getAbsolutePath() + " does not exist or is not a directory" );
        }

        // Create a temporary file 
        File propFile = File.createTempFile( "deploy-svn-ignore.", ".tmp" );
        propFile.deleteOnExit();

        // Write the svn:ignore property value to the temporary file
        BufferedWriter out = new BufferedWriter( new FileWriter( propFile ) );
        out.write( "*.jar" ); // ignore all JAR files
        out.newLine();
        out.write( "*.jnlp" ); // ignore all JNLP files
        out.newLine();
        out.close();

        // For each project directory, set the svn:ignore property for its deploy directory
        String propFilename = propFile.getAbsolutePath();
        PhetProject[] projects = PhetProject.getAllProjects( baseDir );
        for ( int i = 0; i < projects.length; i++ ) {
            String svnCommand = "svn propset svn:ignore --file " + propFilename + " " + projects[i].getDefaultDeployDir().getAbsolutePath();
            System.out.println( svnCommand );
            Runtime.getRuntime().exec( svnCommand );
        }
    }
}
