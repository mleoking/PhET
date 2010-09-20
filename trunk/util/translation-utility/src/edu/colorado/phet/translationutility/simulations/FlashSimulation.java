/* Copyright 2008-2010, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import edu.colorado.phet.translationutility.jar.DocumentAdapter;
import edu.colorado.phet.translationutility.jar.JarUtils;
import edu.colorado.phet.translationutility.jar.DocumentIO.DocumentIOException;
import edu.colorado.phet.translationutility.jar.JarFactory.FlashJarFactory;
import edu.colorado.phet.translationutility.util.FileChooserFactory;

/**
 * FlashSimulation supports translation of Flash-based simulations.
 * Flash simulations use XML document files to store localized strings.
 * There is one XML file per language.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FlashSimulation extends Simulation {
    
    private static final Logger LOGGER = Logger.getLogger( FlashSimulation.class.getCanonicalName() );
    
    public FlashSimulation( String jarFileName, String projectName, String simulationName ) throws SimulationException {
        super( jarFileName, projectName, simulationName, new FlashJarFactory() );
    }
    
    public Properties getStrings( Locale locale ) throws SimulationException {
        
        Properties properties = null;
        String xmlFilename = getStringsResourcePath( locale );
        
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
    
    public String getStringsFileSuffix() {
        return ".xml";
    }
    
    /**
     * Gets the path to the JAR resource that contains localized strings.
     */
    public String getStringsResourcePath( Locale locale ) {
        // XML resources are at the top-level of the JAR, so resource path is the same as resource name
        return getStringsFileName( locale );
    }
    
    /**
     * Gets the name of of the JAR resource for an XML document.
     */
    public String getStringsFileName( Locale locale ) {
        String basename = getStringsBasename();
        String format = "{0}-strings_{1}" + getStringsFileSuffix();  
        Object[] args = { basename, locale };
        return MessageFormat.format( format, args );
    }
    
    /*
     * Gets the basename of the strings file.
     * <p>
     * This is typically the same as the project name, except for common strings.
     * PhET common strings are bundled into their own JAR file for use with translation utility.
     * The JAR file must be built & deployed via a dummy sim named "flash-common-strings", 
     * found in trunk/simulations-flash/simulations.  If the project name is "flash-common-strings",
     * we really want to load the common strings which are in files with basename "common".
     * So we use "common" as the project name.
     */
    private String getStringsBasename() {
        String basename = getProjectName();
        if ( basename.equals( "flash-common-strings" ) ) {
            basename = "common";
        }
        return basename;
    }
    
    public JFileChooser getStringsFileChooser() {
        return FileChooserFactory.createXMLFileChooser();
    }
}
