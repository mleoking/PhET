package edu.colorado.phet.buildtools.util;

import java.io.IOException;
import java.io.OutputStream;

import org.rev6.scf.SshCommand;
import org.rev6.scf.SshConnection;
import org.rev6.scf.SshException;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetWebsite;

/**
 * Commands to make executing SSH commands with exit code checking and output easy
 */
public class SshUtils {
    /**
     * Opens a SSH connection to the website, and executes the specified command
     *
     * @param command The command to execute
     * @param website The website to execute the command on
     * @return Success (whether the command was executed successfully)
     */
    public static boolean executeCommand( PhetWebsite website, String command ) {
        return executeCommands( new String[]{command}, website.getServerHost(), website.getServerAuthenticationInfo( BuildLocalProperties.getInstance() ) );
    }

    /**
     * Opens a SSH connection to the server, and executes the specified commands in order until either one fails or all
     * of them have been executed
     *
     * @param commands The array of commands to be executed
     * @param website  The website to execute the commands on
     * @return Success (whether all of the commands were run successfully)
     */
    public static boolean executeCommands( PhetWebsite website, String[] commands ) {
        return executeCommands( commands, website.getServerHost(), website.getServerAuthenticationInfo( BuildLocalProperties.getInstance() ) );
    }

    /**
     * Opens a SSH connection to the server, and executes the specified command
     *
     * @param command            The command to execute
     * @param host               The host to run the command on
     * @param authenticationInfo ---
     * @return Success (whether the command was executed successfully)
     */
    public static boolean executeCommand( String command, String host, AuthenticationInfo authenticationInfo ) {
        return executeCommands( new String[]{command}, host, authenticationInfo );
    }

    /**
     * Opens a SSH connection to the server, and executes the specified commands in order until either one fails or all
     * of them have been executed
     *
     * @param commands           The array of commands to be executed
     * @param host               The host to run the commands on
     * @param authenticationInfo AuthenticationInfo!!!!!
     * @return Success (whether all of the commands were run successfully)
     */
    public static boolean executeCommands( String[] commands, String host, AuthenticationInfo authenticationInfo ) {
        SshConnection sshConnection = new SshConnection( host, authenticationInfo.getUsername(), authenticationInfo.getPassword() );
        boolean success = true;
        try {
            sshConnection.connect();
            for ( String command : commands ) {
                success = executeCommandOnConnection( sshConnection, command, host );
                if ( !success ) {
                    break;
                }
            }
        }
        catch( SshException e ) {
            if ( e.toString().toLowerCase().indexOf( "auth fail" ) != -1 ) {
                // on tigercat, 3 (9?) unsuccessful login attepts will lock you out
                System.out.println( "Authentication on '" + host + "' has failed, is your username and password correct?  Exiting..." );
                System.exit( 0 );
            }
            e.printStackTrace();
            if ( e.getCause() != null ) {
                System.out.println( "Caused by: " );
                e.getCause().printStackTrace();
            }
            success = false;
        }
        finally {
            sshConnection.disconnect();
        }
        return success;
    }

    private static boolean executeCommandOnConnection( SshConnection sshConnection, String command, String host ) throws SshException {
        final String EXIT_STRING = "EXITCODE=";
        final String END_DELIMITER = "]";

        StringStore store = new StringStore();
        System.out.println( "Executing remote SSH command on " + host + " = " + command );
        SshCommand sshCommand = new SshCommand( command + " 2>&1; echo \"[" + EXIT_STRING + "$?]\"", store );
        sshConnection.executeTask( sshCommand );
        String str = store.toString();
        System.out.println( "Command output is:\n" + str );
        int exitIndex = str.indexOf( EXIT_STRING );
        if ( exitIndex == -1 ) {
            System.out.println( "Warning: could not find exit code in command output, continuing with execution" );
            System.out.println( "Command output is:\n" + str );
            System.out.flush();
            Thread.dumpStack();
            return true;
        }
        int codeIndex = exitIndex + EXIT_STRING.length();
        int codeEndIndex = str.indexOf( END_DELIMITER, codeIndex );
        if ( codeEndIndex == -1 ) {
            System.out.println( "Warning: could not find exit code end delimiter in command output, continuing with execution" );
            System.out.println( "Command output is:\n" + str );
            System.out.flush();
            Thread.dumpStack();
            return true;
        }
        String exitCodeString = str.substring( codeIndex, codeEndIndex );
        if ( Integer.parseInt( exitCodeString ) != 0 ) {
            System.out.println( "Detected exit code: \"" + exitCodeString + "\"" );
            System.out.println( "Exit code was not 0!" );
            System.out.flush();
            Thread.dumpStack();
            return false;
        }
        System.out.flush();
        return true;
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

}
