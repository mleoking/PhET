/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.util.Command;
import edu.colorado.phet.translationutility.util.DocumentAdapter;
import edu.colorado.phet.translationutility.util.Command.CommandException;
import edu.colorado.phet.translationutility.util.DocumentIO.DocumentIOException;

/**
 * FlashSimulation is a Flash-based simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FlashSimulation extends Simulation {
    
    private static final String ARGS_FILENAME = "args.txt";
    private static final String TEST_JAR = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" ) + "phet-test-translation.jar"; // temporary JAR file used to test translations

    private final String _jarFileName;
    private final String _projectName;

    public FlashSimulation( String jarFileName ) {
        super();
        
        _jarFileName = jarFileName;
        
        // project name is the JAR file's basename, without the .jar suffix
        File jarFile = new File( jarFileName );
        String name = jarFile.getName();
        _projectName = name.replace( ".jar", "" );
    }

    public String getProjectName() {
        return _projectName;
    }

    public void test( Properties properties, String languageCode ) throws SimulationException {
        setLocalizedStrings( properties, languageCode );
        try {
            String[] cmdArray = { "java", "-jar", TEST_JAR };
            Command.run( cmdArray, false /* waitForCompletion */ );
        }
        catch ( CommandException e ) {
            throw new SimulationException( e );
        }
    }

    public Properties getLocalizedStrings( String languageCode ) throws SimulationException {
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( _jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( "jar file not found: " + _jarFileName, e );
        }
        
        String xmlFilename = getDocumentResourceName( _projectName, languageCode );
        JarInputStream jarInputStream = null;
        boolean found = false;
        try {
            jarInputStream = new JarInputStream( inputStream );
            
            // look for the properties file
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
            throw new SimulationException( "error reading jar file: " + _jarFileName, e );
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
            throw new SimulationException( "error closing jar file: " + _jarFileName, e );
        }
    
        return properties;
    }

    public void setLocalizedStrings( Properties properties, String languageCode ) throws SimulationException {
        String xmlFilename = getDocumentResourceName( _projectName, languageCode );
        copyJarAndAddProperties( _projectName, languageCode, _jarFileName, TEST_JAR, xmlFilename, properties );
    }

    public Properties importLocalizedStrings( File file ) throws SimulationException {
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

    public void exportLocalizedStrings( Properties properties, File file ) throws SimulationException {
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

    public String getExportFileBasename( String languageCode ) {
        return getXMLFileBasename( _projectName, languageCode );
    }
    
    private static String getXMLFileBasename( String projectName, String languageCode ) {
        return projectName + "-strings_" + languageCode + ".xml";
    }
    
    private static String getDocumentResourceName( String projectName, String languageCode ) {
        // XML resources are at the top-level of the JAR, same as basename
        return getXMLFileBasename( projectName, languageCode );
    }
    
    private static String getHTMLResourceName( String projectName, String languageCode ) {
        return projectName + "_" + languageCode + ".html";
    }
    
    private static void copyJarAndAddProperties( String projectName, String languageCode, String originalJarFileName, String newJarFileName, String xmlFilename, Properties properties ) throws SimulationException {
        
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
            
            // add HTML file used by FlashLauncher
            String html = TUResources.getFlashHTMLTemplate();
            html = html.replaceAll( "@SIM@", projectName );
            html = html.replaceAll( "@LOCALE@", languageCode );
            jarEntry = new JarEntry( htmlResourceName );
            testOutputStream.putNextEntry( jarEntry );
            testOutputStream.write( html.getBytes() );
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
