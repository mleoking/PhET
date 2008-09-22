/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * AbstractSimulation is the base class for all PhET simulations.
 * All PhET simulations are delivered as JAR files.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractSimulation implements ISimulation {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // temporary JAR file used to test translations
    protected static final String TEST_JAR = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" ) + "phet-test-translation.jar";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final String _jarFileName;
    private final Manifest _manifest;
    private final String _projectName;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractSimulation( String jarFileName ) throws SimulationException {
        _jarFileName = jarFileName;
        _manifest = getManifest( jarFileName );
        _projectName = getProjectName( jarFileName );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    protected String getJarFileName() {
        return _jarFileName;
    }
    
    protected Manifest getManifest() {
        return _manifest;
    }
    
    public String getProjectName() {
        return _projectName;
    }
    
    /**
     * Subclasses must implement this to determine the project name from the jar file.
     * 
     * @param jarFileName
     * @return String
     * @throws SimulationException
     */
    protected abstract String getProjectName( String jarFileName ) throws SimulationException;
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Gets the manifest from a jar file.
     * If there is no manifest, this will throw IOException.
     * All JAR files for PhET simulations must have a manifest.
     * 
     * @param jarFileName
     * @return Manifest
     */
    private static Manifest getManifest( String jarFileName ) throws SimulationException {
        Manifest manifest = null;
        try {
            JarFile jarFile = new JarFile( jarFileName );
            JarEntry jarEntry = jarFile.getJarEntry( JarFile.MANIFEST_NAME );
            InputStream inputStream = jarFile.getInputStream( jarEntry );
            manifest = new Manifest( inputStream ); // constructor reads the input stream
            inputStream.close();
            jarFile.close();
        }
        catch ( IOException e ) {
            throw new SimulationException( "error reading manifest from " + jarFileName );
        }
        return manifest;
    }
}
