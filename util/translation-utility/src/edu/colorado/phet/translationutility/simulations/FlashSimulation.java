/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.File;
import java.util.Properties;

/**
 * FlashSimulation is a Flash-based simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FlashSimulation extends Simulation {

    public FlashSimulation() {
        super();
    }

    public String getProjectName() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void test( Properties properties, String languageCode ) throws SimulationException {
        // TODO Auto-generated method stub
    }

    public Properties getLocalizedStrings( String languageCode ) throws SimulationException {
        // TODO Auto-generated method stub
        return null;
    }

    public void setLocalizedStrings( Properties properties, String languageCode ) throws SimulationException {
        // TODO Auto-generated method stub
    }
    
    public Properties importLocalizedStrings( File file ) throws SimulationException {
        //XXX
        return null;
    }

    public void exportLocalizedStrings( Properties properties, File file ) throws SimulationException {
        //XXX
    }
    
    public String getExportFileBasename( String languageCode ) {
        //XXX
        return null;
    }
}
