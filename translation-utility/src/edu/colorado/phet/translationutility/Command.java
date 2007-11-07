/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

/**
 * Command runs an external command via Runtime.exec.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Command {
    
    private static final String ERROR_COMMAND_TERMINATED_ABNORMALLY = TUResources.getString( "error.commandTerminatedAbnormally" );
    private static final String ERROR_COMMAND_FAILED = TUResources.getString( "error.commandFailed" );
    private static final String ERROR_COMMAND_INTERRUPTED = TUResources.getString( "error.commandInterrupted" );
    
    private static boolean _debugOutputEnabled = false;
    
    public static class CommandException extends Exception {
        public CommandException( String message ) {
            super( message );
        }
    }
    
    /* not intended for instantiation */
    private Command() {}
    
    public static void setDebugOutputEnabled( boolean enabled ) {
        _debugOutputEnabled = enabled;
    }

    public static void run( String command, boolean waitForCompletion ) throws CommandException {
        if ( _debugOutputEnabled ) {
            System.out.println( "Command.run command=\"" + command + "\"" );
        }
        try {
            Process process = Runtime.getRuntime().exec( command );
            
            if ( _debugOutputEnabled ) {
                InputStream in = process.getInputStream();
                int c;
                while ( ( c = in.read() ) != -1 ) {
                    System.out.print( (char) c );
                }
                in.close();

                InputStream err = process.getErrorStream();
                while ( ( c = err.read() ) != -1 ) {
                    System.err.print( (char) c );
                }
                err.close();
            }
            
            if ( waitForCompletion ) {
                int exitValue = process.waitFor();
                if ( exitValue != 0 ) {
                    CommandException e = new CommandException( ERROR_COMMAND_TERMINATED_ABNORMALLY + " : " + command );
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new CommandException( ERROR_COMMAND_FAILED + " : " + command );
        }
        catch ( InterruptedException e ) {
            e.printStackTrace();
            throw new CommandException( ERROR_COMMAND_INTERRUPTED + " : " + command );
        } 
    }
}
