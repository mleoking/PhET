// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.colorado.phet.buildtools.jar.FlashJarCreator;
import edu.colorado.phet.buildtools.jar.FlashStringsAdapter;
import edu.colorado.phet.buildtools.jar.JarUtils;
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
        super( jarFileName, projectName, simulationName, new FlashJarCreator() );
    }
    
    public Properties getStrings( Locale locale ) throws SimulationException {
        
        Properties properties = null;
        String xmlFilename = getJarCreator().getStringsFilePath( getProjectName(), locale );
        
        try {
            if ( JarUtils.containsFile( getJarFileName(), xmlFilename ) ) {
                Document document = JarUtils.readDocument( getJarFileName(), xmlFilename );
                properties = FlashStringsAdapter.documentToProperties( document );
                LOGGER.info( "loaded strings from " + xmlFilename );
            }
            else {
                properties = new Properties();
            }
        }
        catch ( ParserConfigurationException e ) {
            throw new SimulationException( "error creating XML document builder" );
        }
        catch ( IOException e ) {
            throw new SimulationException( "error reading XML document" );
        }
        catch ( SAXException e ) {
            throw new SimulationException( "error parsing XML document" );
        }
        
        return properties;
    }

    public Properties loadStrings( File file ) throws SimulationException {
        Properties properties = new Properties();
        try {
            properties = FlashStringsAdapter.readProperties( new FileInputStream( file ) );
        }
        catch ( FileNotFoundException e ) {
            throw new SimulationException( "file not found: " + file.getAbsolutePath() );
        }
        catch ( ParserConfigurationException e ) {
            throw new SimulationException( "error configuring XML parser: " + file.getAbsolutePath() );
        }
        catch ( IOException e ) {
            throw new SimulationException( "error reading XML file: " + file.getAbsolutePath() );
        }
        catch ( SAXException e ) {
            throw new SimulationException( "error parsing XML file: " + file.getAbsolutePath() );
        }
        return properties;
    }
    
    public void saveStrings( Properties properties, File file ) throws SimulationException {
        try {
            String projectName = getProjectName();
            String projectVersion = getProjectVersion( projectName + ".properties" ); // eg, curve-fitting.properties
            String header = getTranslationFileHeader( file.getName(), projectName, projectVersion );
            OutputStream outputStream = new FileOutputStream( file );
            FlashStringsAdapter.writeProperties( properties, header, outputStream );
        }
        catch ( FileNotFoundException e ) {
            throw new SimulationException( "file not found: " + file.getAbsolutePath() );
        }
        catch ( TransformerException e ) {
            throw new SimulationException( "error writing XML file: " + file.getAbsolutePath() );
        }
        catch ( ParserConfigurationException e ) {
            throw new SimulationException( "error configuring XML parser: " + file.getAbsolutePath() );
        }
    }
    
    public JFileChooser getStringsFileChooser() {
        return FileChooserFactory.createXMLFileChooser();
    }
}
