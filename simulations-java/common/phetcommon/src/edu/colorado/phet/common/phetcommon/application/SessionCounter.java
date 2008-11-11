/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.io.*;
import java.security.AccessControlException;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;

/**
 * Counts the number of times that a simulation has been run.
 * Session counts are persistent, residing in .phet/session-counts.properties in the user's home directory.
 * <p>
 * The format of entries is this file is project.flavor=integer, for example:
 * 
 * <code>
 * faraday.faraday=5
 * faraday.magnet-and-compass=2
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SessionCounter {
    
    private static final String SESSION_COUNTS_FILENAME = "session-counts.properties";
    private static final String SESSION_COUNTS_HEADER = "DO NOT EDIT! - counts how many times simulations have been run";
    
    /* singleton */
    private static SessionCounter instance;
    
    private final Integer count;
    
    /* singleton */
    private SessionCounter( String project, String flavor ) {
        count = incrementCount( project, flavor );
    }
    
    /**
     * Initializes and returns the singleton instance.
     * 
     * @param project
     * @param flavor
     * @return
     */
    public static SessionCounter initInstance( String project, String flavor ) {
        if ( instance != null ) {
            throw new RuntimeException( "SessionCounter is already initialized" );
        }
        else {
            instance = new SessionCounter( project, flavor );
        }
        return instance;
    }
    
    /**
     * Gets the singleton instance.
     * 
     * @return
     */
    public static SessionCounter getInstance() {
        return instance;
    }
    
    /**
     * Gets the number of times that a session has been started for this sim.
     * Null if the runtime environment doesn't allow us to count.
     * 
     * @return
     */
    public Integer getCount() {
        return count;
    }
    
    /*
     * Increments the session count for the specified sim.
     * Reads the existing count, increments by 1, and writes the count.
     * 
     * @param project
     * @param flavor
     * @return the updated count, null if the count could not be read/written
     */
    private static Integer incrementCount( String project, String flavor ) {
        Integer newCount = null;
        try {
            String key = getSessionCountKey( project, flavor );

            // read the previous count
            Properties p = readSessionCounts();
            String s = p.getProperty( key );
            Integer previousCount = null;
            if ( s == null ) {
                // no entry in the file, sim has never been run
                previousCount = new Integer( 0 );
            }
            else {
                try {
                    previousCount = new Integer( s );
                }
                catch ( NumberFormatException e ) {
                    e.printStackTrace();
                }
            }

            // increment
            newCount = new Integer( previousCount.intValue() + 1 );

            // write the new count
            p.setProperty( key, newCount.toString() );
            writeSessionCounts( p );
        }
        catch ( AccessControlException e ) {
            // we're running in a situation where we don't can't read or write to the local file system
            System.err.println( "SessionCounter.updateCount, access to local filesystem denied" );//XXX
            newCount = null;
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            newCount = null;
        }
        catch ( IOException e ) {
            e.printStackTrace();
            newCount = null;
        }
        return newCount;
    }
    
    /*
     * Gets the name of the session count file.
     * Do this in a method instead of as a static constant because getting property user.home
     * may cause an AccessControlException if run via JNLP without security permissions.
     */
    private static String getSessionCountFilename() {
        String separator =  System.getProperty( "file.separator" );
        return System.getProperty( "user.home" ) + separator + PhetCommonConstants.PERSISTENCE_DIRNAME + separator + SESSION_COUNTS_FILENAME;
    }
    
    private static String getSessionCountKey( String project, String flavor ) {
        return project + "." + flavor;
    }
    
    private static Properties readSessionCounts() throws IOException {
        Properties p = new Properties();
        File file = new File( getSessionCountFilename() );
        if ( file.exists() ) {
            p.load( new FileInputStream( file ) );
        }
        return p;
    }
    
    private static void writeSessionCounts( Properties p ) throws IOException {
        OutputStream out = new FileOutputStream( getSessionCountFilename() );
        p.store( out, SESSION_COUNTS_HEADER );
    }
}
