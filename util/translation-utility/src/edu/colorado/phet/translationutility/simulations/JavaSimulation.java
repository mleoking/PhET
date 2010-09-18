/* Copyright 2008-2010, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import edu.colorado.phet.translationutility.TUConstants;
import edu.colorado.phet.translationutility.jar.JarUtils;
import edu.colorado.phet.translationutility.jar.JarFactory.JavaJarFactory;
import edu.colorado.phet.translationutility.util.Command;
import edu.colorado.phet.translationutility.util.FileChooserFactory;
import edu.colorado.phet.translationutility.util.Command.CommandException;

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
        String propertiesFileName = JavaJarFactory.getStringsPath( getProjectName(), locale );
        
        try {
            if ( JarUtils.containsFile( getJarFileName(), propertiesFileName ) ) {
                // localized strings exist, load them
                properties = JarUtils.readProperties( getJarFileName(), propertiesFileName );
                LOGGER.info( "loaded strings from " + propertiesFileName );
            }
            else if ( properties == null && locale.equals( TUConstants.ENGLISH_LOCALE ) ) {
                // English strings are in a fallback resource file.
                propertiesFileName = getFallbackStringsPath( getProjectName() );
                properties = JarUtils.readProperties( getJarFileName(), propertiesFileName );
                LOGGER.info( "loaded strings from fallback file " + propertiesFileName );
            }
            else {
                properties = new Properties();
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new SimulationException( "error reading localized strings from " + getJarFileName() );
        }
        
        return properties;
    }

    public Properties loadStrings( File file ) throws SimulationException {
        Properties properties = new Properties();
        try {
            // read properties from file
            InputStream inStream = new FileInputStream( file );
            properties.load( inStream );
            inStream.close();
        }
        catch ( IOException e ) {
            throw new SimulationException( e );
        }
        return properties;
    }
    
    public String getStringsFileSuffix() {
        return JavaJarFactory.getStringsFileSuffix();
    }

    public void saveStrings( Properties properties, File file ) throws SimulationException {
        try {
            String projectName = getProjectName();
            String projectVersion = getProjectVersion( projectName + TUConstants.RESOURCE_PATH_SEPARATOR + projectName + getStringsFileSuffix() ); // eg, faraday/faraday.properties
            String header = getTranslationFileHeader( file.getName(), projectName, projectVersion );
            // write properties to file
            OutputStream outputStream = new FileOutputStream( file );
            properties.store( outputStream, header );
            outputStream.close();
        }
        catch ( IOException e ) {
            throw new SimulationException( e );
        }
    }
    
    public String getStringsFileName( Locale locale ) {
        return JavaJarFactory.getStringsName( getProjectName(), locale );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Gets the path to the fallback JAR resource that contains 
     * English strings for a specified project.
     */
    private String getFallbackStringsPath( String projectName ) {
        return JavaJarFactory.getStringsPath( projectName, null /* locale */ );
    }
    
    /*
     * Creates a test JAR for a specified locale.
     * @param locale the locale
     * @param stringsProperties strings for the locale
     */
    private String createTestJar( Locale locale, Properties stringsProperties ) throws SimulationException {
        final String testJarFileName = TEST_JAR;
        final String originalJarFileName = getJarFileName();
        try {
            JavaJarFactory.createLocalizedJar( originalJarFileName, testJarFileName, locale, stringsProperties, true /* deleteOnExit */ );
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new SimulationException( "failed to create test jar", e );
        }
        return testJarFileName;
    }
    
    public JFileChooser getStringsFileChooser() {
        return FileChooserFactory.createPropertiesFileChooser();
    }
}
