/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import edu.colorado.phet.translationutility.TUResources;

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
     * Gets version information from a properties file in the jar.
     * @param propertiesFileName
     * @return version string, null if we couldn't get the version info
     * @throws SimulationException
     */
    protected String getProjectVersion( String propertiesFileName ) throws SimulationException {
        Properties projectProperties = readPropertiesFromJar( getJarFileName(), propertiesFileName );
        if ( projectProperties == null ) {
            throw new SimulationException( "cannot find the version info file: " + propertiesFileName );
        }
        String major = projectProperties.getProperty( "version.major" );
        String minor = projectProperties.getProperty( "version.minor" );
        String dev = projectProperties.getProperty( "version.dev" );
        String revision = projectProperties.getProperty( "version.revision" );
        Object[] args = { major, minor, dev, revision };
        return MessageFormat.format( "{0}.{1}.{2} ({3})", args );
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
    
    /**
     * Gets a comment that describes the translation file.
     * 
     * @param fileName
     * @param projectName
     * @param projectVersion
     * @return
     */
    protected static String getTranslationFileHeader( String fileName, String projectName, String projectVersion ) {
        String timestamp = new Date().toString(); // timestamp is redundant for a properties file, but is included so that Java and Flash implementations are identical
        return fileName + " created " + timestamp + " using: " + projectName + " " + projectVersion + ", translation-utility " + TUResources.getVersion();
    }
    
    /*
     * Reads a properties file from the specified JAR file.
     * 
     * @param jarFileName
     * @param propertiesFileName
     * @return Properties
     */
    protected static Properties readPropertiesFromJar( String jarFileName, String propertiesFileName ) throws SimulationException {
        
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
                throw new SimulationException( "cannot read localized strings file from jar: " + propertiesFileName, e );
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
