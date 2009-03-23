/* Copyright 2008, University of Colorado */

package edu.colorado.phet.buildtools.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.common.phetcommon.util.StreamReaderThread;

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
            String[] ignorePatterns = new String[]{"*.jar", "*.jnlp", "*.properties", "HEADER", "*.png", "*.jpg", "*.gif", "*.swf", "*.html"};
            setIgnorePatternsOnDir( dir, ignorePatterns );
            System.out.println( "Updated properties on: " + dir.getAbsolutePath() );
        }
    }

    public static void setIgnorePatternsOnDir( File dir, String[] ignorePatterns ) throws IOException, InterruptedException {
        // Write the svn:ignore property value to the temporary file
        // Create a temporary file
        File propFile = File.createTempFile( "deploy-svn-ignore.", ".tmp" );
        propFile.deleteOnExit();
        BufferedWriter out = new BufferedWriter( new FileWriter( propFile ) );

        for ( int i = 0; i < ignorePatterns.length; i++ ) {
            out.write( ignorePatterns[i] );
            out.newLine();
        }
        out.close();

        // For each project directory, set the svn:ignore property for its deploy directory
        String propFilename = propFile.getAbsolutePath();

        //use a command array for non-windows platforms
        String[] svnCommand = new String[]{"svn", "propset", "svn:ignore", "--file", propFilename, dir.getAbsolutePath()};
        System.out.println( "Running: " + Arrays.asList( svnCommand ) );
        Process p = Runtime.getRuntime().exec( svnCommand );
        new StreamReaderThread( p.getErrorStream(), "err" ).start();
        new StreamReaderThread( p.getInputStream(), "out" ).start();
        p.waitFor();
    }
}
