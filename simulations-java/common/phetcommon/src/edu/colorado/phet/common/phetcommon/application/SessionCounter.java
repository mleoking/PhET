/* Copyright 2008-2009, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.io.File;

import edu.colorado.phet.common.phetcommon.util.AbstractPropertiesFile;

/**
 * Counts the number of times that a simulation has been run.
 * Session counts are persistent, residing in .phet/session-counts.properties in the user's home directory.
 * This information is used by the statistics feature.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SessionCounter {
    
    /* singleton */
    private static SessionCounter instance;
    
    private final String project, simulation;
    private final SessionCountsFile file;
    
    /* singleton */
    private SessionCounter( String project, String simulation ) {
        this.project = project;
        this.simulation = simulation;
        file = new SessionCountsFile();
    }
    
    public static SessionCounter initInstance( String project, String simulation ) {
        if ( instance != null ) {
            throw new RuntimeException( "SessionCounter is already initialized" );
        }
        else {
            instance = new SessionCounter( project, simulation );
        }
        return instance;
    }
    
    public static SessionCounter getInstance() {
        return instance;
    }
    
    /**
     * Increments the counts for this sim, and the running total.
     */
    public void incrementCounts() {
        file.setCount( project, simulation, file.getCount( project, simulation ) + 1 );
        file.setCountSince( project, simulation, file.getCountSince( project, simulation ) + 1 );
        file.setTotal( getTotal() + 1 );
    }
    
    /**
     * Resets the counts related to when the sim was last able to send statistics.
     * This should be called after successfully sending a "session" message to PhET.
     */
    public void resetCountSince() {
        file.setCountSince( project, simulation, 0 );
    }
    
    /**
     * Gets the number of sessions ever for this sim.
     * 
     * @return
     */
    public int getCount() {
        return file.getCount( project, simulation );
    }
   
    /**
     * Gets the total number of sessions ever for all sims.
     * 
     * @return
     */
    public int getTotal() {
        return file.getTotal();
    }
    
    /**
     * Gets the number of times that the sim has been started 
     * since the last time that it sent statistics.
     * 
     * @return
     */
    public int getCountSince() {
        return file.getCountSince( project, simulation );
    }
    
    /**
     * Session counts are stored in a file in the persistence directory.
     */
    private static class SessionCountsFile extends AbstractPropertiesFile {
        
        private static final String FILE_HEADER = "DO NOT EDIT! - counts how many times simulations have been run";
        
        private static final String SUFFIX_COUNT = ".count";
        private static final String SUFFIX_SINCE = ".since";
        private static final String KEY_TOTAL_COUNT = "total" + SUFFIX_COUNT;
        
        // eg, faraday.magnet-and-compass.count
        private static String getCountKey( String project, String simulation ) {
            return project + "." + simulation + SUFFIX_COUNT;
        }
        
        // eg, faraday.magnet-and-compass.since
        private static String getSinceKey( String project, String simulation ) {
            return project + "." + simulation + SUFFIX_SINCE;
        }
        
        public SessionCountsFile() {
            super( new File( new PhetPersistenceDir(), "session-counts.properties" ) );
            setHeader( FILE_HEADER );
        }
        
        /**
         * Sets the count that is the total number of times 
         * that a specified sim has ever been run.
         * @param project
         * @param simulation
         * @param count
         */
        public void setCount( String project, String simulation, int count ) {
            setProperty( getCountKey( project, simulation ), count );
        }
        
        /**
         * Gets the count that is the total number of times 
         * that a specified sim has ever been run.
         * @param project
         * @param simulation
         * @param count
         */
        public int getCount( String project, String simulation ) {
            return getPropertyInt( getCountKey( project, simulation ), 0 );
        }
        
        /**
         * Sets the count that is the number of times that a specified 
         * sim has been run since it was last able to send statistics.
         * This value includes the current invocation of the sim.
         * @param project
         * @param simulation
         * @param count
         */
        public void setCountSince( String project, String simulation, int count ) {
            setProperty( getSinceKey( project, simulation ), count );
        }
        
        /**
         * Gets the count that is the number of times that a specified 
         * sim has been run since it was last able to send statistics.
         * This value includes the current invocation of the sim.
         * @param project
         * @param simulation
         * @param count
         */
        public int getCountSince( String project, String simulation ) {
            return getPropertyInt( getSinceKey( project, simulation ), 0 );
        }
        
        /**
         * Sets the count that is the number of times that all 
         * sims have ever been run.
         * @param total
         */
        public void setTotal( int total ) {
            setProperty( KEY_TOTAL_COUNT, total );
        }
        
        /**
         * Gets the count that is the number of times that all 
         * sims have ever been run.
         */
        public int getTotal() {
            return getPropertyInt( KEY_TOTAL_COUNT, 0 );
        }
    }
}
