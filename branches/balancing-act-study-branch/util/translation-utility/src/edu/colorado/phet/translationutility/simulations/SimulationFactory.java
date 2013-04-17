// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.simulations;

import java.io.IOException;

import edu.colorado.phet.buildtools.jar.JarUtils;
import edu.colorado.phet.flashlauncher.util.SimulationProperties;
import edu.colorado.phet.translationutility.simulations.Simulation.SimulationException;

/**
 * SimulationFactory creates an ISimulation based on information in the simulation JAR file.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimulationFactory {
    
    /* not intended for instantiation */
    private SimulationFactory() {}

    /**
     * Creates an ISimulation based on what's in the JAR file.
     * 
     * @param jarFileName
     * @return
     * @throws SimulationException
     */
    public static Simulation createSimulation( String jarFileName ) throws SimulationException {

        // obtain the simulation's properties
        SimulationProperties properties = null;
        try {
            properties = JarUtils.readSimulationProperties( jarFileName );
        }
        catch ( IOException ioe ) {
            ioe.printStackTrace();
            throw new SimulationException( "error reading jar file: " + jarFileName, ioe );
        }

        // create the proper type of simulation
        Simulation simulation = null;
        if ( properties.isFlash() ) {
            simulation = new FlashSimulation( jarFileName, properties.getProject(), properties.getSimulation() );
        }
        else {
            simulation = new JavaSimulation( jarFileName, properties.getProject(), properties.getSimulation() );
        }

        return simulation;
    }
}
