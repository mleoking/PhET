/* Copyright 2008-2010, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import edu.colorado.phet.translationutility.JarUtils;
import edu.colorado.phet.translationutility.jar.DocumentAdapter;
import edu.colorado.phet.translationutility.jar.DocumentIO.DocumentIOException;
import edu.colorado.phet.translationutility.jar.JarFactory.FlashJarFactory;
import edu.colorado.phet.translationutility.util.Command;
import edu.colorado.phet.translationutility.util.FileChooserFactory;
import edu.colorado.phet.translationutility.util.Command.CommandException;

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
    
    public void testStrings( Locale locale, Properties localizedStrings ) throws SimulationException {
        String testJarFileName = createTestJar( locale, localizedStrings );
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
        String xmlFilename = FlashJarFactory.getStringsPath( getProjectName(), locale );
        
        try {
            if ( JarUtils.containsFile( getJarFileName(), xmlFilename ) ) {
                properties = JarUtils.readXMLAsProperties( getJarFileName(), xmlFilename );
                LOGGER.info( "loaded strings from " + xmlFilename );
            }
            else {
                properties = new Properties();
            }
        }
        catch ( IOException ioe ) {
            ioe.printStackTrace();
            throw new SimulationException( ioe );
        }
        catch ( DocumentIOException dioe ) {
            dioe.printStackTrace();
            throw new SimulationException( dioe );
        }
        
        return properties;
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
    
    public String getStringsFileSuffix() {
        return FlashJarFactory.getStringsFileSuffix();
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

    public String getStringsFileName( Locale locale ) {
        return FlashJarFactory.getStringsName( getProjectName(), locale );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Creates a test JAR for a specified locale.
     * @param locale the locale
     * @param stringsProperties strings for the locale
     */
    private String createTestJar( Locale locale, Properties stringsProperties ) throws SimulationException {
        final String testJarFileName = TEST_JAR;
        final String originalJarFileName = getJarFileName();
        try {
            FlashJarFactory.createLocalizedJar( originalJarFileName, testJarFileName, locale, stringsProperties, true /* deleteOnExit */ );
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new SimulationException( "failed to create test jar", e );
        }
        return testJarFileName;
    }
    
    public JFileChooser getStringsFileChooser() {
        return FileChooserFactory.createXMLFileChooser();
    }
}
