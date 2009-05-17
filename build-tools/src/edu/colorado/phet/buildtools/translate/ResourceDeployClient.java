package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.*;

import org.rev6.scf.SshCommand;
import org.rev6.scf.SshConnection;
import org.rev6.scf.SshException;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetServer;
import edu.colorado.phet.buildtools.java.projects.BuildToolsProject;
import edu.colorado.phet.buildtools.util.ScpTo;

import com.jcraft.jsch.JSchException;

public class ResourceDeployClient {

    // TODO: refactor PhetServer so that this type of thing is not necessary
    public static final String PROD_PATH = "/web/chroot/phet/usr/local/apache/htdocs/staging/resources/";

    public static void uploadFile( File resourceFile, File trunk ) {
        AuthenticationInfo authenticationInfo = BuildLocalProperties.getInstance().getProdAuthenticationInfo();
        String temporaryDirName = getTemporaryDirName( resourceFile );
        String temporaryDirPath = PROD_PATH + temporaryDirName;

        dirtyExecute( "mkdir -p -m 775 " + temporaryDirPath + "/resource" );

        // TODO: recognize dirtyExecute failure! (non-essential, ScpTo will fail if dir isn't created)

        try {
            ScpTo.uploadFile( resourceFile, authenticationInfo.getUsername(), PhetServer.PRODUCTION.getHost(),
                              temporaryDirPath + "/resource/" + resourceFile.getName(), authenticationInfo.getPassword() );

            // if this succeeds, then run the ResourceDeployServer
            PhetServer server = PhetServer.PRODUCTION;

            BuildToolsProject buildToolsProject = new BuildToolsProject( new File( trunk, "build-tools" ) );
            String buildScriptDir = server.getServerDeployPath( buildToolsProject );

            String javaCmd = server.getJavaCommand();
            String jarCmd = server.getJarCommand();
            String jarName = buildToolsProject.getDefaultDeployJar().getName();
            String pathToBuildLocalProperties = server.getBuildLocalPropertiesFile();

            String command = javaCmd + " -classpath " + buildScriptDir + "/" + jarName + " " +
                             ResourceDeployServer.class.getName() + " " + jarCmd + " " + pathToBuildLocalProperties +
                             " " + temporaryDirPath;

            dirtyExecute( command );

            dirtyExecute( "cat " + temporaryDirPath + "/status.txt" );
            
        }
        catch( JSchException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static String getTemporaryDirName( File resourceFile ) {
        String ret = resourceFile.getName();
        ret = ret.replaceAll( "[^a-zA-Z0-9]", "-" );
        ret = String.valueOf( ( new Date() ).getTime() ) + "_" + ret;
        return ret;
    }

    public static void main( String[] args ) {
        if ( args.length == 0 ) {
            System.out.println( "You must specify the path to trunk!" );
            return;
        }

        File trunk;

        try {
            trunk = ( new File( args[0] ) ).getCanonicalFile();
            BuildLocalProperties.initRelativeToTrunk( trunk );
            //testSSH();
        }
        catch( IOException e ) {
            e.printStackTrace();
            return;
        }

        final JFileChooser fileChooser = new JFileChooser();
        int ret = fileChooser.showOpenDialog( null );

        if ( ret != JFileChooser.APPROVE_OPTION ) {
            System.out.println( "File was not selected, aborting" );
            return;
        }

        File resourceFile = fileChooser.getSelectedFile();

        System.out.println( "Selected resource file: " + resourceFile.getName() );

        uploadFile( resourceFile, trunk );


    }


    public static void dirtyExecute( String command ) {
        PhetServer server = PhetServer.PRODUCTION;
        AuthenticationInfo authenticationInfo = BuildLocalProperties.getInstance().getProdAuthenticationInfo();
        SshConnection sshConnection = new SshConnection( server.getHost(), authenticationInfo.getUsername(), authenticationInfo.getPassword() );
        try {
            sshConnection.connect();
            sshConnection.executeTask( new SshCommand( command ) );
        }
        catch( SshException e ) {
            if ( e.toString().toLowerCase().indexOf( "auth fail" ) != -1 ) {
                // TODO: check if authentication fails, don't try logging in again
                // on tigercat, 3 (9?) unsuccessful login attepts will lock you out
                System.out.println( "Authentication on '" + server.getHost() + "' has failed, is your username and password correct?  Exiting..." );
                System.exit( 0 );
            }
            e.printStackTrace();
        }
        finally {
            sshConnection.disconnect();
        }
    }


    public static void testSSH() {
        AuthenticationInfo authenticationInfo = BuildLocalProperties.getInstance().getProdAuthenticationInfo();
        PhetServer server = PhetServer.PRODUCTION;

        SshConnection sshConnection = new SshConnection( server.getHost(), authenticationInfo.getUsername(), authenticationInfo.getPassword() );
        try {
            sshConnection.connect();
            sshConnection.executeTask( new SshCommand( "cat /web/chroot/phet/usr/local/apache/htdocs/README.txt" ) );
            System.out.println( "Success?" );
        }
        catch( SshException e ) {
            e.printStackTrace();
            if ( e.toString().toLowerCase().indexOf( "auth fail" ) != -1 ) {
                System.out.println( "Authentication on '" + server.getHost() + "' has failed, is your username and password correct?  Exiting..." );
                System.exit( 0 );
            }
        }
        finally {
            System.out.println( "Disconnecting" );
            sshConnection.disconnect();
        }

    }

}
