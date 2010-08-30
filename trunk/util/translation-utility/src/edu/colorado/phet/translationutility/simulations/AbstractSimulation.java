/* Copyright 2008-2009, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import edu.colorado.phet.translationutility.TUConstants;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.util.JarUtils;

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
    protected static final String TEST_JAR = TUResources.getTmpdir() + TUConstants.FILE_PATH_SEPARATOR + "phet-test-translation.jar";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final String _jarFileName;
    private final Manifest _manifest;
    private final String _projectName;
    private final String _simulationName;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractSimulation( String jarFileName, String projectName, String simulationName ) throws SimulationException {
        _jarFileName = jarFileName;
        _manifest = getManifest( jarFileName );
        _projectName = projectName;
        _simulationName = simulationName;
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
    
    public String getSimulationName() {
        return _simulationName;
    }
    
    /**
     * Gets version information from a properties file in the jar.
     * @param propertiesFileName
     * @return version string, null if we couldn't get the version info
     * @throws SimulationException
     */
    protected String getProjectVersion( String propertiesFileName ) throws SimulationException {
        Properties projectProperties = JarUtils.readPropertiesFromJar( getJarFileName(), propertiesFileName );
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
}
