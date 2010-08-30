/* Copyright 2010, University of Colorado */

package edu.colorado.phet.translationutility.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import edu.colorado.phet.translationutility.simulations.ISimulation.SimulationException;

/**
 * Utility methods related to JAR files.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JarUtils {

    /* not intended for instantiation */
    private JarUtils() {}
    
    /**
     * Does a JAR contain a specified file?
     * @param jarFileName
     * @param fileName
     * @return true or false
     * @throws SimulationException
     */
    public static boolean containsFile( String jarFileName, String fileName ) throws SimulationException {
        
        boolean found = false;
        
        // open the jar file
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( "jar file not found: " + jarFileName, e );
        }
        
        // look for file
        JarInputStream jarInputStream = null;
        try {
            jarInputStream = new JarInputStream( inputStream );
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                if ( jarEntry.getName().equals( fileName ) ) {
                    found = true;
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
        
        return found;
    }
    
    /**
     * Reads a properties file from the specified JAR file.
     * 
     * @param jarFileName
     * @param propertiesFileName
     * @return Properties
     */
    public static Properties readPropertiesFromJar( String jarFileName, String propertiesFileName ) throws SimulationException {
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( "jar file not found: " + jarFileName, e );
        }
        
        JarInputStream jarInputStream = null;
        boolean found = false;
        try {
            jarInputStream = new JarInputStream( inputStream );
            
            // look for the properties file
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                if ( jarEntry.getName().equals( propertiesFileName ) ) {
                    found = true;
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
        
        Properties properties = null;
        if ( found ) {
            properties = new Properties();
            try {
                properties.load( jarInputStream );
            }
            catch ( IOException e ) {
                e.printStackTrace();
                throw new SimulationException( "cannot read properties file from jar: " + propertiesFileName, e );
            }
        }
        
        try {
            jarInputStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new SimulationException( "error closing jar file: " + jarFileName, e );
        }
    
        return properties;
    }
}
