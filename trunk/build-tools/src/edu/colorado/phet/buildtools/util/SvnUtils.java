package edu.colorado.phet.buildtools.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.SVNStatusChecker;
import edu.colorado.phet.common.phetcommon.util.*;
import edu.colorado.phet.common.phetcommon.util.StreamReaderThread;

public class SvnUtils {
    public static boolean commitProject( PhetProject project, AuthenticationInfo auth ) {
        String message = project.getName() + ": deployed version " + project.getFullVersionString();
        String path = project.getProjectDir().getAbsolutePath();
        String[] args = new String[]{"svn", "commit", "--non-interactive", "--username", auth.getUsername(), "--password", auth.getPassword(), "--message", message, path};
        //TODO: verify that SVN repository revision number now matches what we wrote to the project properties file
        ProcessOutputReader.ProcessExecResult a = ProcessOutputReader.exec( args );
        if ( a.getTerminatedNormally() ) {
            System.out.println( "Finished committing new version file with message: " + message + " output/err=" );
            System.out.println( a.getOut() );
            System.out.println( a.getErr() );
            System.out.println( "Finished committing new version file with message: " + message );
            return true;
        }
        else {
            System.out.println( "Abnormal termination: " + a );
            return false;
        }
    }

    public static boolean isProjectUpToDate( PhetProject project ) {
        return new SVNStatusChecker().isUpToDate( project );
    }

    public static void setIgnorePatternsOnDir( File dir, String[] ignorePatterns ) throws IOException, InterruptedException {
        // Write the svn:ignore property value to the temporary file
        // Create a temporary file
        File propFile = File.createTempFile( "deploy-svn-ignore.", ".tmp" );
        propFile.deleteOnExit();
        BufferedWriter out = new BufferedWriter( new FileWriter( propFile ) );

        for ( String ignorePattern : ignorePatterns ) {
            out.write( ignorePattern );
            out.newLine();
        }
        out.close();

        // For each project directory, set the svn:ignore property for its deploy directory
        String propFilename = propFile.getAbsolutePath();

        //use a command array for non-windows platforms
        String[] svnCommand = new String[]{"svn", "propset", "svn:ignore", "--file", propFilename, dir.getAbsolutePath()};
        System.out.println( "Running: " + Arrays.asList( svnCommand ) );
        Process p = Runtime.getRuntime().exec( svnCommand );
        new edu.colorado.phet.common.phetcommon.util.StreamReaderThread( p.getErrorStream(), "err" ).start();
        new StreamReaderThread( p.getInputStream(), "out" ).start();
        p.waitFor();
    }
}
