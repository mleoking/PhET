/* Copyright 2008-2010, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.*;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import edu.colorado.phet.common.phetcommon.view.util.StringUtil;
import edu.colorado.phet.flashlauncher.FlashLauncher;
import edu.colorado.phet.flashlauncher.util.SimulationProperties;
import edu.colorado.phet.translationutility.util.Command;
import edu.colorado.phet.translationutility.util.DocumentAdapter;
import edu.colorado.phet.translationutility.util.FileChooserFactory;
import edu.colorado.phet.translationutility.util.JarUtils;
import edu.colorado.phet.translationutility.util.Command.CommandException;
import edu.colorado.phet.translationutility.util.DocumentIO.DocumentIOException;

/**
 * FlashSimulation supports translation of Flash-based simulations.
 * Flash simulations use XML document files to store localized strings.
 * There is one XML file per language.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FlashSimulation extends AbstractSimulation {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String COMMON_STRINGS_PROJECT = "flash-common-strings";
    private static final String COMMON_STRINGS_BASENAME = "common";

    private static final Logger LOGGER = Logger.getLogger( FlashSimulation.class.getCanonicalName() );
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public FlashSimulation( String jarFileName, String projectName, String simulationName ) throws SimulationException {
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
        String xmlFilename = getStringPath( getProjectName(), locale );
        Properties p = readDocumentFromJar( getJarFileName(), xmlFilename );
        LOGGER.info( "loaded strings from " + xmlFilename );
        return p;
    }

    public Properties loadStrings( File file ) throws SimulationException {
        Properties properties = new Properties();
        try {
            properties = DocumentAdapter.readProperties( new FileInputStream( file ) );
        }
        catch ( FileNotFoundException e ) {
            throw new SimulationException( "file not found: " + file.getAbsolutePath(), e );
        }
        catch ( DocumentIOException e ) {
            throw new SimulationException( e );
        }
        return properties;
    }

    public void saveStrings( Properties properties, File file ) throws SimulationException {
        try {
            String projectName = getProjectName();
            String projectVersion = getProjectVersion( projectName + ".properties" ); // eg, curve-fitting.properties
            String header = getTranslationFileHeader( file.getName(), projectName, projectVersion );
            OutputStream outputStream = new FileOutputStream( file );
            DocumentAdapter.writeProperties( properties, header, outputStream );
        }
        catch ( FileNotFoundException e ) {
            throw new SimulationException( "file not found: " + file.getAbsolutePath(), e );
        }
        catch ( DocumentIOException e ) {
            throw new SimulationException( e );
        }
    }

    public String getStringFileName( Locale locale ) {
        return getStringsName( getProjectName(), locale );
    }
    
    /*
     * Gets the project name, based on the JAR file name.
     * The JAR file name may or may not contain a language code.
     * For example, acceptable file names for the "curve-fit" project are curve-fit.jar and curve-fit_fr.jar.
     * Returns null if the project name wasn't found.
     */
    protected String getProjectName( String jarFileName ) throws SimulationException {
        String projectName = null;
        File jarFile = new File( jarFileName );
        String name = jarFile.getName();
        int index = name.indexOf( "_" );
        if ( index != -1 ) {
            // eg, curve-fit_fr.jar
            projectName = name.substring( 0, index );
        }
        else {
            // eg, curve-fit.jar
            index = name.indexOf( "." );
            if ( index != -1 ) {
                projectName = name.substring( 0, index );
            }
        }
        return projectName;
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Gets the path to the JAR resource that contains localized strings.
     */
    private String getStringPath( String projectName, Locale locale ) {
        // XML resources are at the top-level of the JAR, so resource path is the same as resource name
        return getStringsName( projectName, locale );
    }
    
    /*
     * Gets the name of of the JAR resource for an XML document.
     */
    private String getStringsName( String projectName, Locale locale ) {
        String stringsBasename = getStringsBasename( projectName );
        String format = "{0}-strings_{1}" + getStringFileSuffix();  // eg, curve-fit-strings_en.xml
        Object[] args = { stringsBasename, locale };
        return MessageFormat.format( format, args );
    }
    
    public String getStringFileSuffix() {
        return ".xml";
    }
    
    /*
     * Gets the basename of the strings file.
     * <p>
     * This is typically the same as the project name, except for common strings.
     * PhET common strings are bundled into their own JAR file for use with translation utility.
     * The JAR file must be built & deployed via a dummy sim named COMMON_STRINGS_PROJECT, 
     * found in trunk/simulations-flash/simulations.  If the project name is COMMON_STRINGS_PROJECT,
     * we really want to load the common strings which are in files with basename COMMON_STRINGS_BASENAME.
     * So we use COMMON_STRINGS_BASENAME as the project name.
     */
    protected static String getStringsBasename( String projectName ) {
        String basename = projectName;
        if ( basename.equals( COMMON_STRINGS_PROJECT ) ) {
            basename = COMMON_STRINGS_BASENAME;
        }
        return basename;
    }
    
    /*
     * Reads an XML document from the specified JAR file, and converts it to Properties.
     * The XML document contains localized strings.
     */
    private static Properties readDocumentFromJar( String jarFileName, String xmlFilename ) throws SimulationException {
        
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
            
            // look for the XML file
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                if ( jarEntry.getName().equals( xmlFilename ) ) {
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
                properties = DocumentAdapter.readProperties( jarInputStream );
            }
            catch ( DocumentIOException e ) {
                throw new SimulationException( e );
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
     * Copies a JAR file and adds (or replaces) an XML file and a file that identifies the language code for FlashLauncher.
     * The XML file contains localized strings.
     * The original JAR file is not modified.
     */
    private String createTestJar( Properties properties, Locale locale ) throws SimulationException {
        
        final String testJarFileName = TEST_JAR;
        final String originalJarFileName = getJarFileName();
        final String projectName = getProjectName();
        
        if ( originalJarFileName.equals( testJarFileName  ) ) {
            throw new IllegalArgumentException( "originalJarFileName and newJarFileName must be different" );
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
        String xmlFilename = getStringPath( projectName, locale );
        String[] exclude = {
                JarFile.MANIFEST_NAME,
                "META-INF/.*\\.SF", "META-INF/.*\\.RSA", "META-INF/.*\\.DSA", /* signing information */
                xmlFilename,
                FlashLauncher.ARGS_FILENAME,
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
            
            // copy all entries from input to output, skipping the properties file, manifest, args.txt & HTML file
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
            
            // add string properties file to output
            jarEntry = new JarEntry( xmlFilename );
            testOutputStream.putNextEntry( jarEntry );
            DocumentAdapter.writeProperties( properties, "created by " + getClass().getName(), testOutputStream );
            testOutputStream.closeEntry();
            
            // add flash-launcher-args.txt
            addFlashLauncherArgsFile( testOutputStream, projectName, locale );
            
            // add simulation.properties, see #2463
            jarEntry = new JarEntry( SimulationProperties.FILENAME );
            testOutputStream.putNextEntry( jarEntry );
            SimulationProperties sp = new SimulationProperties( getProjectName(), getSimulationName(), locale, SimulationProperties.TYPE_FLASH );
            sp.store( testOutputStream, "created by " + getClass().getName() );
            testOutputStream.closeEntry();
            
            // close the streams
            jarInputStream.close();
            testOutputStream.close();
        }
        catch ( IOException e ) {
            throw new SimulationException( "failed to add localized strings to jar file: " + testJarFileName, e );
        }
        catch ( DocumentIOException e ) {
            throw new SimulationException( e );
        }
        
        return testJarFileName;
    }
    
    /*
     * Add flash-launcher-args.txt file, used by older FlashLauncher.
     * Delete this method when all sims have been redeployed with simulation.properties.
     * See #2463.
     * @deprecated
     */
    private void addFlashLauncherArgsFile( JarOutputStream outputStream, String projectName, Locale locale ) throws IOException {
        JarEntry jarEntry = new JarEntry( FlashLauncher.ARGS_FILENAME );
        outputStream.putNextEntry( jarEntry );
        String args = createFlashLauncherArgsString( projectName, locale );
        outputStream.write( args.getBytes() );
        outputStream.closeEntry();
    }
    
    /*
     * Creates the contents of the flash-launcher-args.txt file.
     * Format: projectName language country
     * If country doesn't have a value, use "null".
     */
    private static String createFlashLauncherArgsString( String projectName, Locale locale ) {
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String s = projectName + " " + language;
        if ( country == null || country.length() == 0 ) {
            s += " " + "null";
        }
        else {
            s += " " + country;
        }
        return s;
    }
    
    public JFileChooser getStringFileChooser() {
        return FileChooserFactory.createXMLFileChooser();
    }
}
