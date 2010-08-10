package edu.colorado.phet.buildtools.test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.rev6.scf.SshCommand;
import org.rev6.scf.SshConnection;
import org.rev6.scf.SshException;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.OldPhetServer;
import edu.colorado.phet.buildtools.util.ScpTo;
import edu.colorado.phet.buildtools.util.SshUtils;

import com.jcraft.jsch.JSchException;

public class TestSshCommand {
    private static void sendSSH( OldPhetServer server, AuthenticationInfo authenticationInfo ) {
        String remotePathDir = server.getServerDeployPath() + "/test-permissions/0.00.03";
        SshUtils.executeCommand( "mkdir -p -m 775 " + remotePathDir, server.getHost(), authenticationInfo );

        try {
            File tmpFile = File.createTempFile( "prefix", ".suffix" );
            ScpTo.uploadFile( tmpFile, authenticationInfo.getUsername(), server.getHost(), remotePathDir + "/" + "unknowndir/prefix.suffix", authenticationInfo.getPassword() );
        }
        catch( JSchException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private static boolean execute( SshConnection sshConnection, String command ) throws SshException {
        final String EXIT_STRING = "EXITCODE=";
        final String END_DELIMITER = "]";

        StringStore store = new StringStore();
        System.out.println( "Executing command = " + command );
        sshConnection.executeTask( new SshCommand( command + " 2>&1; echo \"[" + EXIT_STRING + "$?]\"", store ) );
        String str = store.toString();
        System.out.println( "Command output is:\n" + str );
        int exitIndex = str.indexOf( EXIT_STRING );
        if ( exitIndex == -1 ) {
            System.out.println( "Warning: could not find exit code in command output, continuing with execution" );
            System.out.flush();
            return true;
        }
        int codeIndex = exitIndex + EXIT_STRING.length();
        int codeEndIndex = str.indexOf( END_DELIMITER, codeIndex );
        if ( codeEndIndex == -1 ) {
            System.out.println( "Warning: could not find exit code end delimiter in command output, continuing with execution" );
            System.out.flush();
            return true;
        }
        String exitCodeString = str.substring( codeIndex, codeEndIndex );
        System.out.println( "Detected exit code: \"" + exitCodeString + "\"" );
        if ( Integer.parseInt( exitCodeString ) != 0 ) {
            System.out.println( "Exit code was not 0!" );
            System.out.flush();
            return false;
        }
        System.out.flush();
        return true;
    }

    private static String getParentDir( String remotePathDir ) {
        if ( remotePathDir.endsWith( "/" ) ) {
            remotePathDir = remotePathDir.substring( 0, remotePathDir.length() - 1 );
        }
        int x = remotePathDir.lastIndexOf( '/' );
        return remotePathDir.substring( 0, x );
    }

    private static class StringStore extends OutputStream {
        private StringBuffer buf = new StringBuffer();

        public void write( int i ) throws IOException {
            buf.append( (char) i );
        }

        public String toString() {
            return buf.toString();
        }
    }

    public static void main( String[] args ) {
        File trunk = new File( args[0] );
        sendSSH( OldPhetServer.DEVELOPMENT, BuildLocalProperties.getInstanceRelativeToTrunk( trunk ).getDevAuthenticationInfo() );
    }
}
