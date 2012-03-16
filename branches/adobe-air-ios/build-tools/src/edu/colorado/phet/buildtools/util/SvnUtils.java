package edu.colorado.phet.buildtools.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.SVNStatusChecker;
import edu.colorado.phet.common.phetcommon.util.StreamReaderThread;
import edu.colorado.phet.common.phetcommon.view.util.XMLUtils;

public class SvnUtils {
    public static boolean commitProject( PhetProject project, AuthenticationInfo auth ) {
        String message = project.getName() + ": deployed version " + project.getFullVersionString();
        String path = project.getProjectDir().getAbsolutePath();
        String[] args = new String[] { "svn", "commit", "--non-interactive", "--username", auth.getUsername(), "--password", auth.getPassword(), "--message", message, path };
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
        String[] svnCommand = new String[] { "svn", "propset", "svn:ignore", "--file", propFilename, dir.getAbsolutePath() };
        System.out.println( "Running: " + Arrays.asList( svnCommand ) );
        Process p = Runtime.getRuntime().exec( svnCommand );
        new edu.colorado.phet.common.phetcommon.util.StreamReaderThread( p.getErrorStream(), "err" ).start();
        new StreamReaderThread( p.getInputStream(), "out" ).start();
        p.waitFor();
    }

    /**
     * Returns whether or not each path is up to the particular revision
     *
     * @param revision The revision we need the entries to be
     * @param paths    Various paths to check together
     * @return Whether or not all of the path's revisions were the same as the specified revision
     */
    public static boolean verifyRevision( int revision, String[] paths ) {
        String out = null;
        try {
            AuthenticationInfo auth = BuildLocalProperties.getInstance().getRespositoryAuthenticationInfo();
            List<String> args = new LinkedList<String>();
            args.add( "svn" );
            args.add( "info" );
            args.add( "--xml" ); // so we can easily parse the XML
            args.add( "--non-interactive" ); // so it doesn't pause for input
            args.add( "--username" );
            args.add( auth.getUsername() );
            args.add( "--password" );
            args.add( auth.getPassword() );
            for ( String path : paths ) {
                args.add( path );
            }
            ProcessOutputReader.ProcessExecResult result = ProcessOutputReader.exec( args.toArray( new String[0] ) );

            out = result.getOut();
            Document document = XMLUtils.toDocument( out );
            NodeList entries = document.getElementsByTagName( "entry" );

            for ( int i = 0; i < entries.getLength(); i++ ) {
                Node entryNode = entries.item( i );
                Element entryElement = (Element) entryNode;
                String revisionString = entryElement.getAttribute( "revision" );
                if ( Integer.parseInt( revisionString ) == revision ) {
                    System.out.println( entryElement.getAttribute( "path" ) + " has the correct revision " + revisionString );
                }
                else {
                    System.out.println( "Warning: " + entryElement.getAttribute( "path" ) + " has the incorrect revision "
                                        + revisionString + ", it should be " + revision );
                    return false;
                }
            }
        }
        catch ( TransformerException e ) {
            e.printStackTrace();
            System.out.println( "Caused by the XML:\n" + out );
            return false;
        }
        catch ( ParserConfigurationException e ) {
            e.printStackTrace();
            System.out.println( "Caused by the XML:\n" + out );
            return false;
        }
        return true;
    }
}
