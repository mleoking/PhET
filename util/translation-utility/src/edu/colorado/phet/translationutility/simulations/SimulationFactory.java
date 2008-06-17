/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import edu.colorado.phet.translationutility.simulations.ISimulation.SimulationException;

/**
 * SimulationFactory creates a simulation based on the input file type and contents.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimulationFactory {
    
    private SimulationFactory() {}
    
    public static ISimulation createSimulation( String filename ) throws SimulationException {

        ISimulation simulation = null;

        if ( isJar( filename ) ) {
            if ( isFlash( filename ) ) {
                simulation = new FlashSimulation( filename );
            }
            else {
                simulation = new JavaSimulation( filename );
            }
        }
        else {
            throw new SimulationException( "not a jar file: " + filename );
        }

        return simulation;
    }
    
    private static boolean isJar( String jarFileName ) {
        return jarFileName.endsWith( ".jar" );
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
            throw new SimulationException( "jar file not found: " + jarFileName, e );
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
            throw new SimulationException( "error reading jar file: " + jarFileName, e );
        }
        
        return containsSWF;
    }
}
