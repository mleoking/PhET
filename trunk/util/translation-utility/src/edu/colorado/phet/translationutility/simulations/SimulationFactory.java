/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.simulations.Simulation.SimulationException;

/**
 * SimulationFactory creates a simulation based on the input file type and contents.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimulationFactory {
    
    private static final String ERROR_CANNOT_OPEN_JAR = TUResources.getString( "error.cannotOpenJar" );
    private static final String ERROR_CANNOT_READ_JAR = TUResources.getString( "error.cannotReadJar" );

    private SimulationFactory() {}
    
    public static Simulation createSimulation( String jarFileName ) throws SimulationException {

        Simulation simulation = null;

        /*
         * Assume we have a JAR file.
         * If the JAR contains a .swf file, then it's a Flash sim.
         * Otherwise, it's a Java sim.
         */
        if ( isFlash( jarFileName ) ) {
            simulation = new FlashSimulation( jarFileName );
        }
        else {
            simulation = new JavaSimulation( jarFileName );
        }

        return simulation;
    }
    
    /*
     * JAR file is a Flash sim if it contains a .swf file. 
     */
    private static boolean isFlash( String jarFileName ) throws SimulationException {
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( ERROR_CANNOT_OPEN_JAR + " : " + jarFileName, e );
        }
        
        JarInputStream jarInputStream = null;
        boolean containsSWF = false;
        try {
            jarInputStream = new JarInputStream( inputStream );
            
            // look for the properties file
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                if ( jarEntry.getName().endsWith( ".swf" ) ) {
                    containsSWF = true;
                    break;
                }
                else {
                    jarEntry = jarInputStream.getNextJarEntry();
                }
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new SimulationException( ERROR_CANNOT_READ_JAR + " : " + jarFileName, e );
        }
        
        return containsSWF;
    }
}
