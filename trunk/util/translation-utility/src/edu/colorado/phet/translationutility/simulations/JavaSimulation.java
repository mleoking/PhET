/* Copyright 2008-2010, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.translationutility.TUConstants;
import edu.colorado.phet.translationutility.jar.JarUtils;
import edu.colorado.phet.translationutility.jar.JarCreator.JavaJarCreator;
import edu.colorado.phet.translationutility.util.FileChooserFactory;

/**
 * JavaSimulation supports of Java-based simulations.
 * Java simulations use Java properties files to store localized strings.
 * There is one properties file per locale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JavaSimulation extends Simulation {
    
    private static final Logger LOGGER = Logger.getLogger( JavaSimulation.class.getCanonicalName() );
    
    public JavaSimulation( String jarFileName, String projectName, String simulationName ) throws SimulationException {
        super( jarFileName, projectName, simulationName, new JavaJarCreator() );
    }
    
    public Properties getStrings( Locale locale ) throws SimulationException {
        
        Properties properties = null;
        String propertiesFileName = getStringsFilePath( locale );
        
        try {
            if ( JarUtils.containsFile( getJarFileName(), propertiesFileName ) ) {
                // localized strings exist, load them
                properties = JarUtils.readProperties( getJarFileName(), propertiesFileName );
                LOGGER.info( "loaded strings from " + propertiesFileName );
            }
            else if ( properties == null && locale.equals( TUConstants.ENGLISH_LOCALE ) ) {
                // English strings are in a fallback resource file.
                propertiesFileName = getStringsFilePath( null /* locale */ );
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
    
    public String getStringsFileSuffix() {
        return ".properties";
    }
    
    /**
     * Gets the path to the JAR resource that contains localized strings for 
     * a specified project and locale. If locale is null, the fallback resource
     * path is returned. 
     */
    public String getStringsFilePath( Locale locale ) {
        String dirName = getStringsRootName();
        String fileName = getStringsFileBasename( locale );
        return dirName + "/localization/" + fileName;
    }
    
    /**
     * Gets the basename of the JAR resource that contains localized strings for 
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
    public String getStringsFileBasename( Locale locale ) {
        String rootName = getStringsRootName();
        String basename = null;
        if ( locale == null ) {
            basename = rootName + "-strings" + getStringsFileSuffix(); // fallback basename contains no language code
        }
        else {
            String localeString = LocaleUtils.localeToString( locale );
            basename = rootName + "-strings_" + localeString + getStringsFileSuffix();
        }
        return basename;
    }
    
    /*
     * Gets the root name of the resource that contains localized strings.
     * <p>
     * This is typically the same as the project name, except for common strings.
     * PhET common strings are bundled into their own JAR file for use with Translation Utility.
     * The JAR file must be built & deployed via a dummy sim named "java-common-strings", 
     * found in trunk/simulations-flash/simulations.  If the project name is "java-common-strings",
     * we really want to load the common strings which are in files with root name "phetcommon".
     * So we use "phetcommon" as the project name.
     */
    private String getStringsRootName() {
        String rootName = getProjectName();
        if ( rootName.equals( "java-common-strings" ) ) {
            rootName = "phetcommon";
        }
        return rootName;
    }

    public JFileChooser getStringsFileChooser() {
        return FileChooserFactory.createPropertiesFileChooser();
    }
}
