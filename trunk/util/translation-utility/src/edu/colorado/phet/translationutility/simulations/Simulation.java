/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.File;
import java.util.Properties;

/**
 * Simulation is the base class for all types of simulations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Simulation {
    
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
    
    protected Simulation() {}
    
    public abstract String getProjectName();

    public abstract void test( Properties properties, String languageCode ) throws SimulationException ;

    public abstract Properties getLocalizedStrings( String languageCode ) throws SimulationException ;

    public abstract void setLocalizedStrings( Properties properties, String languageCode ) throws SimulationException;
    
    public abstract Properties importLocalizedStrings( File file ) throws SimulationException;

    public abstract void exportLocalizedStrings( Properties properties, File file ) throws SimulationException;
    
    public abstract String getExportFileBasename( String languageCode );
}
