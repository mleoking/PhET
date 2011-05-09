// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import edu.colorado.phet.buildtools.jar.JarUtils;
import edu.colorado.phet.buildtools.jar.JavaJarCreator;
import edu.colorado.phet.translationutility.TUConstants;
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
        String propertiesFileName = getJarCreator().getStringsFilePath( getProjectName(), locale );
        
        try {
            if ( JarUtils.containsFile( getJarFileName(), propertiesFileName ) ) {
                // localized strings exist, load them
                properties = JarUtils.readProperties( getJarFileName(), propertiesFileName );
                LOGGER.info( "loaded strings from " + propertiesFileName );
            }
            else if ( properties == null && locale.equals( TUConstants.ENGLISH_LOCALE ) ) {
                // English strings are in a fallback resource file.
                propertiesFileName = getJarCreator().getStringsFilePath( getProjectName(), null /* locale */ );
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
            String projectVersion = getProjectVersion( projectName + TUConstants.RESOURCE_PATH_SEPARATOR + projectName + getJarCreator().getStringsFileSuffix() ); // eg, faraday/faraday.properties
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
    
    public JFileChooser getStringsFileChooser() {
        return FileChooserFactory.createPropertiesFileChooser();
    }
}
