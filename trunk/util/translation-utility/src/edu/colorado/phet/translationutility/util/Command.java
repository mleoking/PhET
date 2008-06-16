/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.util;

import java.io.IOException;
import java.io.InputStream;

import edu.colorado.phet.translationutility.TUResources;

/**
 * Command runs an external command via Runtime.exec.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Command {
    
    private static final String ERROR_COMMAND_TERMINATED_ABNORMALLY = TUResources.getString( "error.commandTerminatedAbnormally" );
    private static final String ERROR_COMMAND_FAILED = TUResources.getString( "error.commandFailed" );
    private static final String ERROR_COMMAND_INTERRUPTED = TUResources.getString( "error.commandInterrupted" );
    
    private static final boolean DEBUG_COMMAND_STRING = true;
    private static final boolean DEBUG_COMMAND_STREAMS = false;
    
    /**
     * All exceptions caught by Command will be mapped to CommandException.
     */
    public static class CommandException extends Exception {

        public CommandException( String message ) {
            super( message );
        }

        public CommandException( String message, Throwable cause ) {
            super( message, cause );
        }
    }
    
    /* not intended for instantiation */
    private Command() {}
    
    /**
     * Runs a command using Process.exec.
     * We use the String[] form of Process.exec so that Java won't parse the command args.
     * This allows us to properly handle pathnames that contain whitespace.
     * Java would normally break a String command into separate args at each occurence of whitespace.
     * 
     * @param cmdArray
     * @param waitForCompletion
     * @throws CommandException
     */
    public static void run( String[] cmdArray, boolean waitForCompletion ) throws CommandException {
        
        String command = "";
        for ( int i = 0; i < cmdArray.length; i++ ) {
            command += cmdArray[i];
            if ( i < cmdArray.length - 1 ) {
                command += " ";
            }
        }
        if ( DEBUG_COMMAND_STRING ) {
            System.out.println( "Command.run " + command );
        }
        
        try {
            Process process = Runtime.getRuntime().exec( cmdArray );
            
            if ( DEBUG_COMMAND_STREAMS ) {
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
            throw new CommandException( ERROR_COMMAND_FAILED + " : " + command, e );
        }
        catch ( InterruptedException e ) {
            e.printStackTrace();
            throw new CommandException( ERROR_COMMAND_INTERRUPTED + " : " + command, e );
        } 
    }
}
