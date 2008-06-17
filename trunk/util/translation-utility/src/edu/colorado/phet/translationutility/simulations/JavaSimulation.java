/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.util.Command;
import edu.colorado.phet.translationutility.util.PropertiesIO;
import edu.colorado.phet.translationutility.util.Command.CommandException;
import edu.colorado.phet.translationutility.util.PropertiesIO.PropertiesIOException;

/**
 * JavaSimulation supports of Java-based simulations.
 * Java simulations use Java properties files to store localized strings.
 * There is one properties file per language.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JavaSimulation implements ISimulation {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // temporary JAR file used to test translations
    private static final String TEST_JAR = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" ) + "phet-test-translation.jar";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final String _jarFileName;
    private final String _projectName;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public JavaSimulation( String jarFileName ) throws SimulationException {
        super();
        _jarFileName = jarFileName;
        
        String[] commonProjectNames = TUResources.getCommonProjectNames();
        _projectName = getSimulationProjectName( jarFileName, commonProjectNames );
    }
    
    //----------------------------------------------------------------------------
    // Public interface
    //----------------------------------------------------------------------------
    
    public String getProjectName() {
        return _projectName;
    }
    
    public void testStrings( Properties properties, String languageCode ) throws SimulationException {
        String propertiesFileName = getPropertiesResourceName( _projectName, languageCode );
        writePropertiesToJarCopy( _jarFileName, TEST_JAR, propertiesFileName, properties );
        try {
            String[] cmdArray = { "java", "-jar", "-Duser.language=" + languageCode, TEST_JAR };
            Command.run( cmdArray, false /* waitForCompletion */ );
        }
        catch ( CommandException e ) {
            throw new SimulationException( e );
        }
    }

    public Properties getStrings( String languageCode ) throws SimulationException {
        return readPropertiesFromJar( _jarFileName, _projectName, languageCode );
    }

    public Properties loadStrings( File file ) throws SimulationException {
        Properties properties = null;
        try {
            properties = PropertiesIO.read( file );
        }
        catch ( PropertiesIOException e ) {
            throw new SimulationException( e );
        }
        return properties;
    }

    public void saveStrings( Properties properties, File file ) throws SimulationException {
        try {
            PropertiesIO.write( properties, file );
        }
        catch ( PropertiesIOException e ) {
            throw new SimulationException( e );
        }
    }
    
    public String getSubmitBasename( String languageCode ) {
        return getPropertiesResourceBasename( _projectName, languageCode );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Gets the base name of the JAR resource that contains localized strings for 
     * a specified project and language.  If the language code is null, the default 
     * name (English) is returned.
     * 
     * @param projectName
     * @param languageCode
     * @return
     */
    private static String getPropertiesResourceBasename( String projectName, String languageCode ) {
        String baseName = null;
        if ( languageCode == null || languageCode == "en" ) {
            String format = "{0}-strings.properties";  // eg, faraday-strings.properties
            Object[] args = { projectName, languageCode };
            baseName = MessageFormat.format( format, args );
        }
        else {
            String format = "{0}-strings_{1}.properties"; // eg, faraday-strings_ar.properties
            Object[] args = { projectName, languageCode };
            baseName = MessageFormat.format( format, args );
        }
        return baseName;
    }
    
    /*
     * Gets the full name of the JAR resource that contains localized strings for 
     * a specified project and language.  If the language code is null, the default 
     * name (English) is returned.
     */
    private static String getPropertiesResourceName( String projectName, String languageCode ) {
        return projectName + "/localization/" + getPropertiesResourceBasename( projectName, languageCode );
    }
    
    /*
     * Gets the name of the simulation project used to create the JAR file.
     * We search for localization files in the JAR file.
     * The first localization file that does not belong to a common project is assumed
     * to belong to the simulation, and we extract the project name from the localization file name.
     * 
     * @param jarFileName
     * @param commonProjectNames
     * @return String
     * @throws JarIOException
     */
    private static String getSimulationProjectName( String jarFileName, String[] commonProjectNames ) throws SimulationException {
        
        String projectName = null;
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( "jar file not found: " + jarFileName, e );
        }
        
        JarInputStream jarInputStream = null;
        String localizationWildcard = getPropertiesResourceName( ".*" /* match for any project name */, "en" );
        try {
            jarInputStream = new JarInputStream( inputStream );
            
            // look for the properties files
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                String jarEntryName = jarEntry.getName();
                if ( jarEntryName.matches( localizationWildcard ) ) {
                    boolean commonMatch = false;
                    for ( int i = 0; i < commonProjectNames.length; i++ ) {
                        // for example, phetcommon/localization/phetcommon-strings.properties
                        String commonProjectFileName = getPropertiesResourceName( commonProjectNames[i], "en" );
                        if ( jarEntryName.matches( commonProjectFileName ) ) {
                            commonMatch = true;
                            break;
                        }
                    }
                    if ( !commonMatch ) {
                        int index = jarEntryName.indexOf( '/' );
                        projectName = jarEntryName.substring( 0, index );
                        break;
                    }
                }
                jarEntry = jarInputStream.getNextJarEntry();
            }
            
            jarInputStream.close();
        }
        catch ( IOException e ) {
            throw new SimulationException( "error reading jar file: " + jarFileName, e );
        }
        
        if ( projectName == null ) {
            throw new SimulationException( "could not determine this simulation's project name: " + jarFileName );
        }
        
        return projectName;
    }
    
    /*
     * Reads a properties file from the specified JAR file.
     * The properties file contains localized strings.
     * 
     * @param jarFileName
     * @param projectName
     * @param languageCode
     * @return Properties
     */
    private static Properties readPropertiesFromJar( String jarFileName, String projectName, String languageCode ) throws SimulationException {
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( "jar file not found: " + jarFileName, e );
        }
        
        String propertiesFileName = getPropertiesResourceName( projectName, languageCode );
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
    
    /*
     * Copies a JAR file and adds (or replaces) a properties file.
     * The properties file contains localized strings.
     * The original JAR file is not modified.
     * 
     * @param originalJarFileName
     * @param newJarFileName
     * @param propertiesFileName
     * @param properties
     */
    private static void writePropertiesToJarCopy( String originalJarFileName, String newJarFileName, String propertiesFileName, Properties properties ) throws SimulationException {
        
        if ( originalJarFileName.equals( newJarFileName  ) ) {
            throw new IllegalArgumentException( "originalJarFileName and newJarFileName must be different" );
        }
        
        File jarFile = new File( originalJarFileName );
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFile );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( "jar file not found: " + originalJarFileName, e );
        }
        
        File testFile = new File( newJarFileName );
        testFile.deleteOnExit(); // temporary file, delete when the VM exits
        try {
            // input comes from the original JAR file
            JarInputStream jarInputStream = new JarInputStream( inputStream ); // throws IOException
            Manifest manifest = jarInputStream.getManifest();
            if ( manifest == null ) {
                throw new SimulationException( "jar file is missing its manifest: " + originalJarFileName );
            }
            
            // output goes to test JAR file
            OutputStream outputStream = new FileOutputStream( testFile );
            JarOutputStream testOutputStream = new JarOutputStream( outputStream, manifest );
            
            // copy all entries from input to output, skipping the properties file
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                if ( !jarEntry.getName().equals( propertiesFileName ) ) {
                    testOutputStream.putNextEntry( jarEntry );
                    byte[] buf = new byte[1024];
                    int len;
                    while ( ( len = jarInputStream.read( buf ) ) > 0 ) {
                        testOutputStream.write( buf, 0, len );
                    }
                    testOutputStream.closeEntry();
                }
                jarEntry = jarInputStream.getNextJarEntry();
            }
            
            // add properties file to output
            jarEntry = new JarEntry( propertiesFileName );
            testOutputStream.putNextEntry( jarEntry );
            String header = propertiesFileName;
            properties.store( testOutputStream, header );
            testOutputStream.closeEntry();
            
            // close the streams
            jarInputStream.close();
            testOutputStream.close();
        }
        catch ( IOException e ) {
            testFile.delete();
            e.printStackTrace();
            throw new SimulationException( "cannot add localized strings to jar file: " + newJarFileName, e );
        }
    }
}
