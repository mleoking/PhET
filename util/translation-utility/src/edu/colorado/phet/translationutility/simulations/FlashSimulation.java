/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import edu.colorado.phet.translationutility.util.Command;
import edu.colorado.phet.translationutility.util.DocumentAdapter;
import edu.colorado.phet.translationutility.util.Command.CommandException;
import edu.colorado.phet.translationutility.util.DocumentIO.DocumentIOException;

/**
 * FlashSimulation supports translation of Flash-based simulations.
 * Flash simulations use XML document files to store localized strings.
 * There is one XML file per language.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FlashSimulation implements ISimulation {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TEST_JAR = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" ) + "phet-test-translation.jar"; // temporary JAR file used to test translations
    private static final String ARGS_FILENAME = "flash-launcher-args.txt";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final String _jarFileName;
    private final String _projectName;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public FlashSimulation( String jarFileName ) throws SimulationException {
        super();
        _jarFileName = jarFileName;
        _projectName = getSimulationProjectName( jarFileName );
    }
    
    //----------------------------------------------------------------------------
    // Public interface
    //----------------------------------------------------------------------------
    
    public String getProjectName() {
        return _projectName;
    }

    public void testStrings( Properties properties, String languageCode ) throws SimulationException {
        String xmlFilename = getDocumentResourceName( _projectName, languageCode );
        writeDocumentToJarCopy( _projectName, languageCode, _jarFileName, TEST_JAR, xmlFilename, properties );
        try {
            String[] cmdArray = { "java", "-jar", TEST_JAR };
            Command.run( cmdArray, false /* waitForCompletion */ );
        }
        catch ( CommandException e ) {
            throw new SimulationException( e );
        }
    }

    public Properties getStrings( String languageCode ) throws SimulationException {
        return readDocumentFromJar( _jarFileName, _projectName, languageCode );
    }

    public Properties loadStrings( File file ) throws SimulationException {
        Properties properties = null;
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
            OutputStream outputStream = new FileOutputStream( file );
            DocumentAdapter.writeProperties( properties, outputStream );
        }
        catch ( FileNotFoundException e ) {
            throw new SimulationException( "file not found: " + file.getAbsolutePath(), e );
        }
        catch ( DocumentIOException e ) {
            throw new SimulationException( e );
        }
    }

    public String getSubmitBasename( String languageCode ) {
        return getDocumentResourceBasename( _projectName, languageCode );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Gets the base name of of the JAR resource for an XML document.
     */
    private static String getDocumentResourceBasename( String projectName, String languageCode ) {
        String format = "{0}-strings_{1}.xml";  // eg, curve-fit-strings_en.xml
        Object[] args = { projectName, languageCode };
        return MessageFormat.format( format, args );
    }
    
    /*
     * Gets the JAR resource name for an XML document.
     */
    private static String getDocumentResourceName( String projectName, String languageCode ) {
        // XML resources are at the top-level of the JAR, so resource name is the same as basename
        return getDocumentResourceBasename( projectName, languageCode );
    }
    
    /*
     * Gets the JAR resource name for an HTML file.
     * @param projectName
     * @param languageCode
     * @return
     */
    private static String getHTMLResourceName( String projectName, String languageCode ) {
        String format = "{0}_{1}.html"; // eg, curve-fit_en.html
        Object[] args = { projectName, languageCode };
        return MessageFormat.format( format, args );
    }
    
    /*
     * Gets the project name, based on the JAR file name.
     * The JAR file name may or may not contain a language code.
     * For example, acceptable file names for the "curve-fit" project are curve-fit.jar and curve-fit_fr.jar.
     * 
     * @param jarFileName
     * @return String
     */
    private static String getSimulationProjectName( String jarFileName ) throws SimulationException {
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
            else {
                throw new SimulationException( "cannot determine project name" );
            }
        }
        return projectName;
    }
    
    /*
     * Reads an XML document from the specified JAR file, and converts it to Properties.
     * The XML document contains localized strings.
     * 
     * @param jarFileName
     * @param projectName
     * @param languageCode
     * @return Properties
     */
    private static Properties readDocumentFromJar( String jarFileName, String projectName, String languageCode ) throws SimulationException {
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( "jar file not found: " + jarFileName, e );
        }
        
        String xmlFilename = getDocumentResourceName( projectName, languageCode );
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
     * 
     * @param originalJarFileName
     * @param newJarFileName
     * @param propertiesFileName
     * @param properties
     * @throws JarIOException
     */
    private static void writeDocumentToJarCopy( String projectName, String languageCode, String originalJarFileName, String newJarFileName, String xmlFilename, Properties properties ) throws SimulationException {
        
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
            
            // output goes to test JAR file
            OutputStream outputStream = new FileOutputStream( testFile );
            JarOutputStream testOutputStream = new JarOutputStream( outputStream );
            
            // copy all entries from input to output, skipping the properties file & args.txt & HTML file
            String htmlResourceName = getHTMLResourceName( projectName, languageCode );
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                if ( !jarEntry.getName().equals( xmlFilename ) && !jarEntry.getName().equals( ARGS_FILENAME ) && !jarEntry.getName().equals( htmlResourceName ) ) {
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
            jarEntry = new JarEntry( xmlFilename );
            testOutputStream.putNextEntry( jarEntry );
            DocumentAdapter.writeProperties( properties, testOutputStream );
            testOutputStream.closeEntry();
            
            // add args.txt file used by FlashLauncher
            jarEntry = new JarEntry( ARGS_FILENAME );
            testOutputStream.putNextEntry( jarEntry );
            String args = projectName + " " + languageCode;
            testOutputStream.write( args.getBytes() );
            testOutputStream.closeEntry();
            
            // close the streams
            jarInputStream.close();
            testOutputStream.close();
        }
        catch ( IOException e ) {
            throw new SimulationException( "failed to add localized strings to jar file: " + newJarFileName, e );
        }
        catch ( DocumentIOException e ) {
            throw new SimulationException( e );
        }
    }
}
