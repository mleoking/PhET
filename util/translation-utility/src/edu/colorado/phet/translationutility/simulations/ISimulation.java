/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.File;
import java.util.Properties;

/**
 * ISimulation is the interface implemented by all types of simulations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ISimulation {
    
    /**
     * Exception type thrown by methods of this class.
     * Provides a layer of abstraction for more general types of exceptions.
     */
    public static class SimulationException extends Exception {
        public SimulationException( Throwable cause ) {
            super( cause );
        }
        public SimulationException( String message ) {
            super( message );
        }
        public SimulationException( String message, Throwable cause ) {
            super( message, cause );
        }
    }
    
    /**
     * Gets the simulation's project name.
     * @return
     */
    public String getProjectName();
    
    /**
     * Tests a set of localized strings by running the simulation.
     * 
     * @param properties the localized strings
     * @param languageCode
     * @throws SimulationException
     */
    public void testStrings( Properties properties, String languageCode ) throws SimulationException;

    /**
     * Gets the localized strings for a specified language.
     * 
     * @param languageCode
     * @return
     * @throws SimulationException
     */
    public Properties getStrings( String languageCode ) throws SimulationException;

    /**
     * Loads a set of localized strings from a file.
     * 
     * @param file
     * @return
     * @throws SimulationException
     */
    public Properties loadStrings( File file ) throws SimulationException;

    /**
     * Saves a set of localized strings to a file.
     * 
     * @param properties
     * @param file
     * @throws SimulationException
     */
    public void saveStrings( Properties properties, File file ) throws SimulationException;
    
    /**
     * Gets the base name of the file for submitting localized strings to PhET.
     * 
     * @param languageCode
     * @return
     */
    public String getSubmitBasename( String languageCode );
}
