/* Copyright 2008-2010, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.*;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import edu.colorado.phet.common.phetcommon.application.JARLauncher;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.view.util.StringUtil;
import edu.colorado.phet.flashlauncher.util.SimulationProperties;
import edu.colorado.phet.translationutility.TUConstants;
import edu.colorado.phet.translationutility.util.Command;
import edu.colorado.phet.translationutility.util.FileChooserFactory;
import edu.colorado.phet.translationutility.util.JarUtils;
import edu.colorado.phet.translationutility.util.PropertiesIO;
import edu.colorado.phet.translationutility.util.Command.CommandException;
import edu.colorado.phet.translationutility.util.PropertiesIO.PropertiesIOException;

/**
 * JavaSimulation supports of Java-based simulations.
 * Java simulations use Java properties files to store localized strings.
 * There is one properties file per locale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JavaSimulation extends AbstractSimulation {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String COMMON_STRINGS_PROJECT = "java-common-strings";
    private static final String COMMON_STRINGS_BASENAME = "phetcommon";
    private static final String PREFERRED_FONTS = "phetcommon/localization/phetcommon-fonts.properties";

    private static final Logger LOGGER = Logger.getLogger( JavaSimulation.class.getCanonicalName() );
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public JavaSimulation( String jarFileName, String projectName, String simulationName ) throws SimulationException {
        super( jarFileName, projectName, simulationName );
    }
    
    //----------------------------------------------------------------------------
    // Public interface
    //----------------------------------------------------------------------------
    
    public void testStrings( Properties properties, Locale locale ) throws SimulationException {
        String testJarFileName = createTestJar( properties, locale );
        try {
            String[] cmdArray = { "java", "-jar", testJarFileName };
            Command.run( cmdArray, false /* waitForCompletion */ );
        }
        catch ( CommandException e ) {
            throw new SimulationException( e );
        }
    }

    public Properties getStrings( Locale locale ) throws SimulationException {
        
        Properties properties = null;
        
        try {
            String propertiesFileName = getStringsPath( getProjectName(), locale );
            if ( JarUtils.containsFile( getJarFileName(), propertiesFileName ) ) {
                // localized strings exist, load them
                properties = JarUtils.readProperties( getJarFileName(), propertiesFileName );
            }
            else if ( properties == null && locale.equals( TUConstants.ENGLISH_LOCALE ) ) {
                // English strings are in a fallback resource file.
                propertiesFileName = getFallbackStringsPath( getProjectName() );
                properties = JarUtils.readProperties( getJarFileName(), propertiesFileName );
            }
            else {
                properties = new Properties();
            }
            LOGGER.info( "loaded strings from " + propertiesFileName );
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new SimulationException( "error reading localized strings from " + getJarFileName() );
        }
        
        return properties;
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
    
    public String getStringFileSuffix() {
        return ".properties";
    }

    public void saveStrings( Properties properties, File file ) throws SimulationException {
        try {
            String projectName = getProjectName();
            String projectVersion = getProjectVersion( projectName + TUConstants.RESOURCE_PATH_SEPARATOR + projectName + getStringFileSuffix() ); // eg, faraday/faraday.properties
            String header = getTranslationFileHeader( file.getName(), projectName, projectVersion );
            PropertiesIO.write( properties, header, file );
        }
        catch ( PropertiesIOException e ) {
            throw new SimulationException( e );
        }
    }
    
    public String getStringFileName( Locale locale ) {
        return getStringsName( getProjectName(), locale );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Gets the path to the fallback JAR resource that contains 
     * English strings for a specified project.
     */
    private String getFallbackStringsPath( String projectName ) {
        return getStringsPath( projectName, null /* locale */ );
    }
    
    /*
     * Gets the path to the JAR resource that contains localized strings for 
     * a specified project and locale. If locale is null, the fallback resource
     * path is returned. 
     */
    private String getStringsPath( String projectName, Locale locale ) {
        String dirName = getStringsBasename( projectName );
        String fileName = getStringsName( projectName, locale );
        return dirName + TUConstants.RESOURCE_PATH_SEPARATOR + "localization" + TUConstants.RESOURCE_PATH_SEPARATOR + fileName;
    }
    
    /*
     * Gets the name of the JAR resource that contains localized strings for 
     * a specified project and locale. For example, faraday-strings_es.properties
     * <p>
     * If locale is null, the name of the fallback resource is returned.
     * The fallback name does not contain a locale, and contains English strings.
     * For example: faraday-strings.properties
     * <p>
     * NOTE: Support for the fallback name is provided for backward compatibility.
     * All Java simulations should migrate to the convention of including "en" in the 
     * resource name of English localization files.
     */
    private String getStringsName( String projectName, Locale locale ) {
        String stringsBasename = getStringsBasename( projectName );
        String basename = null;
        if ( locale == null ) {
            basename = stringsBasename + "-strings" + getStringFileSuffix(); // fallback basename contains no language code
        }
        else {
            String localeString = LocaleUtils.localeToString( locale );
            basename = stringsBasename + "-strings_" + localeString + getStringFileSuffix();
        }
        return basename;
    }
    
    /*
     * Gets the basename of the resource that contains localized strings.
     * <p>
     * This is typically the same as the project name, except for common strings.
     * PhET common strings are bundled into their own JAR file for use with translation utility.
     * The JAR file must be built & deployed via a dummy sim named COMMON_STRINGS_PROJECT, 
     * found in trunk/simulations-flash/simulations.  If the project name is COMMON_STRINGS_PROJECT,
     * we really want to load the common strings which are in files with basename COMMON_STRINGS_BASENAME.
     * So we use COMMON_STRINGS_BASENAME as the project name.
     */
    private static String getStringsBasename( String projectName ) {
        String basename = projectName;
        if ( basename.equals( COMMON_STRINGS_PROJECT ) ) {
            basename = COMMON_STRINGS_BASENAME;
        }
        return basename;
    }
    
    /*
     * Creates a test JAR by doing the following:
     * 1. copies the original JAR file
     * 2. adds or replaces a properties file containing localized strings
     * 3. adds or replaces a properties file used by JARLauncher to set the locale
     * 4. removes files related to digital signatures
     * 
     * The original JAR file is not modified.
     */
    private String createTestJar( Properties stringsProperties, Locale locale ) throws SimulationException {

        final String testJarFileName = TEST_JAR;
        final String originalJarFileName = getJarFileName();
        
        if ( originalJarFileName.equals( testJarFileName ) ) {
            throw new IllegalArgumentException( "originalJarFileName and newJarFileName must be different" );
        }
        
        // read JARLaucher properties from JAR
        Properties jarLauncherProperties = null;
        try {
            jarLauncherProperties = JarUtils.readProperties( originalJarFileName, JARLauncher.PROPERTIES_FILE_NAME );
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new SimulationException( "error reading " + JARLauncher.PROPERTIES_FILE_NAME + " from " + originalJarFileName );
        }
        if ( jarLauncherProperties == null ) {
            throw new SimulationException( "Cannot read " + JARLauncher.PROPERTIES_FILE_NAME + ". Are you using the most current simulation JAR from the PhET website?" );
        }
        
        // change the properties related to locale
        jarLauncherProperties.setProperty( "language", locale.getLanguage() );
        String country = locale.getCountry();
        if ( country != null && country.length() > 0 ) {
            jarLauncherProperties.setProperty( "country", country );
        }
        
        // open the original JAR file
        JarInputStream jarInputStream = null;
        try {
            jarInputStream = JarUtils.openJar( originalJarFileName );
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new SimulationException( "error opening jar file: " + originalJarFileName, e );
        }
        
        // regular expressions for files to exclude while copying the JAR
        String stringsFileName = getStringsPath( getProjectName(), locale );
        String[] exclude = {
                JarFile.MANIFEST_NAME,
                "META-INF/.*\\.SF", "META-INF/.*\\.RSA", "META-INF/.*\\.DSA", /* signing information */
                stringsFileName,
                JARLauncher.PROPERTIES_FILE_NAME,
                PREFERRED_FONTS,
                SimulationProperties.FILENAME
        };
        
        // create the test JAR file
        File testFile = new File( testJarFileName );
        testFile.deleteOnExit(); // temporary file, delete when the VM exits
        try {
            // output goes to test JAR file
            OutputStream outputStream = new FileOutputStream( testFile );
            Manifest manifest = getManifest();
            JarOutputStream testOutputStream = new JarOutputStream( outputStream, manifest );
            
            // copy all entries from input to output, excluding some entries
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                
                if ( !StringUtil.matches( exclude, jarEntry.getName() ) ) {
                    
                    testOutputStream.putNextEntry( jarEntry );
                    byte[] buf = new byte[1024];
                    int len;
                    while ( ( len = jarInputStream.read( buf ) ) > 0 ) {
                        testOutputStream.write( buf, 0, len );
                    }
                    testOutputStream.closeEntry();
                }
                else {
                    LOGGER.info( "copying jar, skipping " + jarEntry.getName() );
                }
                jarEntry = jarInputStream.getNextJarEntry();
            }
            
            // add properties file to output
            jarEntry = new JarEntry( stringsFileName );
            testOutputStream.putNextEntry( jarEntry );
            stringsProperties.store( testOutputStream, "created by " + JavaSimulation.class.getName() );
            testOutputStream.closeEntry();
            
            // add JARLauncher properties to output
            jarEntry = new JarEntry( JARLauncher.PROPERTIES_FILE_NAME );
            testOutputStream.putNextEntry( jarEntry );
            jarLauncherProperties.store( testOutputStream, "created by " + JavaSimulation.class.getName() );
            testOutputStream.closeEntry();
            
            // use Translation Utility's preferred fonts, #1653
            Properties preferredFonts = loadProperties( PREFERRED_FONTS );
            jarEntry = new JarEntry( PREFERRED_FONTS );
            testOutputStream.putNextEntry( jarEntry );
            preferredFonts.store( testOutputStream, "created by " + JavaSimulation.class.getName() );
            testOutputStream.closeEntry();
            
            // add simulation.properties, see #2463
            jarEntry = new JarEntry( SimulationProperties.FILENAME );
            testOutputStream.putNextEntry( jarEntry );
            SimulationProperties sp = new SimulationProperties( getProjectName(), getSimulationName(), locale, SimulationProperties.TYPE_JAVA );
            sp.store( testOutputStream, "created by " + getClass().getName() );
            testOutputStream.closeEntry();
            
            // close the streams
            jarInputStream.close();
            testOutputStream.close();
        }
        catch ( IOException e ) {
            testFile.delete();
            e.printStackTrace();
            throw new SimulationException( "cannot add localized strings to jar file: " + testJarFileName, e );
        }
        
        return testJarFileName;
    }
    
    /*
     * Loads properties from a resource in the translation utility jar.
     */
    private Properties loadProperties( String resourceName ) throws IOException {
        Properties properties = new Properties();
        InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( resourceName );
        if ( inStream != null ) {
            properties.load( inStream );
        }
        return properties;
    }
    
    public JFileChooser getStringFileChooser() {
        return FileChooserFactory.createPropertiesFileChooser();
    }
}
