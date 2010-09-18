/* Copyright 2008-2010, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import edu.colorado.phet.translationutility.JarUtils;
import edu.colorado.phet.translationutility.TUConstants;
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
    protected static final String TEST_JAR = TUResources.getTmpdir() + TUConstants.FILE_PATH_SEPARATOR + "phet-test-translation.jar";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final String jarFileName;
    private final Manifest manifest;
    private final String projectName;
    private final String simulationName;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractSimulation( String jarFileName, String projectName, String simulationName ) throws SimulationException {
        this.jarFileName = jarFileName;
        this.manifest = getManifest( jarFileName );
        this.projectName = projectName;
        this.simulationName = simulationName;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    protected String getJarFileName() {
        return jarFileName;
    }
    
    protected Manifest getManifest() {
        return manifest;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public String getSimulationName() {
        return simulationName;
    }
    
    /**
     * Gets version information from a properties file in the jar.
     * @param propertiesFileName
     * @return version string, null if we couldn't get the version info
     * @throws SimulationException
     */
    protected String getProjectVersion( String propertiesFileName ) throws SimulationException {
        
        // read properties file that contains the version info
        Properties projectProperties = null;
        try {
            projectProperties = JarUtils.readProperties( getJarFileName(), propertiesFileName );
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new SimulationException( "error reading " + propertiesFileName + " from " + getJarFileName() );
        }
        if ( projectProperties == null ) {
            throw new SimulationException( "cannot find the version info file: " + propertiesFileName );
        }
        
        // extract the version info
        String major = projectProperties.getProperty( "version.major" );
        String minor = projectProperties.getProperty( "version.minor" );
        String dev = projectProperties.getProperty( "version.dev" );
        String revision = projectProperties.getProperty( "version.revision" );
        
        // format the version name
        Object[] args = { major, minor, dev, revision };
        return MessageFormat.format( "{0}.{1}.{2} ({3})", args );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Gets the manifest from a jar file.
     * All JAR files for PhET simulations must have a manifest.
     * 
     * @param jarFileName
     * @return Manifest
     * @throws SimulationException if there was no manifest
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
