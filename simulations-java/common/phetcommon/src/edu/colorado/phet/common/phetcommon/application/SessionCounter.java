/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.io.*;
import java.security.AccessControlException;
import java.util.Properties;

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
            
            SessionCountsFile file = new SessionCountsFile();

            // read the previous count
            Properties p = file.readCounts();
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
            file.writeCounts( p );
        }
        catch ( AccessControlException accessControlException ) {
            System.out.println( "SessionCounter.incrementCount: access to local filesystem denied" );
            newCount = null;
        }
        catch ( IOException e ) {
            e.printStackTrace();
            newCount = null;
        }
        return newCount;
    }
    
    private static String getSessionCountKey( String project, String flavor ) {
        return project + "." + flavor;
    }
    
    /**
     * Session counts are stored in a file in the persistence directory.
     */
    private static class SessionCountsFile extends File {
        
        private static final String SESSION_COUNTS_HEADER = "DO NOT EDIT! - counts how many times simulations have been run";

        /**
         * @throws AccessControlException 
         */
        public SessionCountsFile() throws AccessControlException {
            super( new PhetPersistenceDir(), "session-counts.properties" );
        }

        public Properties readCounts() throws IOException {
            Properties p = new Properties();
            if ( exists() ) {
                p.load( new FileInputStream( this ) );
            }
            return p;
        }
        
        public void writeCounts( Properties p ) throws IOException {
            getParentFile().mkdirs();
            OutputStream outStream = new FileOutputStream( this );
            p.store( outStream, SESSION_COUNTS_HEADER );
        }
    }
}
