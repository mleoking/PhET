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

    private static final String ERROR_CANNOT_OPEN_JAR = TUResources.getString( "error.cannotOpenJar" );
    private static final String ERROR_CANNOT_CLOSE_JAR = TUResources.getString( "error.cannotCloseJar" );
    private static final String ERROR_CANNOT_READ_JAR = TUResources.getString( "error.cannotReadJar" );
    private static final String ERROR_CANNOT_EXTRACT_PROPERTIES_FILE = TUResources.getString( "error.cannotExtractPropertiesFile" );
    private static final String ERROR_CANNOT_INSERT_PROPERTIES_FILE = TUResources.getString( "error.cannotInsertPropertiesFile" );
    private static final String ERROR_MISSING_MANIFEST = TUResources.getString( "error.missingManifest" );
    private static final String ERROR_IMPORT = TUResources.getString( "error.import" );
    private static final String ERROR_RUN_JAR = TUResources.getString( "error.runJar" );
    private static final String ERROR_IMPORT_FILE_NOT_FOUND = "import file not found";
    private static final String ERROR_PROPERTIES_TO_XML = "failed to convert Properties to XML";
    private static final String ERROR_EXPORT_FILE_NOT_FOUND = "export file not found";
    
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
            throw new SimulationException( ERROR_RUN_JAR + " : " + TEST_JAR, e );
        }
    }

    public Properties getLocalizedStrings( String languageCode ) throws SimulationException {
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( _jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( ERROR_CANNOT_OPEN_JAR + " : " + _jarFileName, e );
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
            throw new SimulationException( ERROR_CANNOT_READ_JAR + " : " + _jarFileName, e );
        }
        
        Properties properties = null;
        if ( found ) {
            properties = new Properties();
            try {
                properties = DocumentAdapter.readProperties( jarInputStream );
            }
            catch ( DocumentIOException e ) {
                e.printStackTrace();
                throw new SimulationException( ERROR_CANNOT_EXTRACT_PROPERTIES_FILE + " : " + xmlFilename, e );
            }
        }
        
        try {
            jarInputStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new SimulationException( ERROR_CANNOT_CLOSE_JAR + " : " + _jarFileName, e );
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
            throw new SimulationException( ERROR_IMPORT_FILE_NOT_FOUND + " : " + file.getAbsolutePath(), e );
        }
        catch ( DocumentIOException e ) {
            throw new SimulationException( ERROR_IMPORT + " : " + file.getAbsolutePath(), e );
        }
        return properties;
    }

    public void exportLocalizedStrings( Properties properties, File file ) throws SimulationException {
        try {
            OutputStream outputStream = new FileOutputStream( file );
            DocumentAdapter.writeProperties( properties, outputStream );
        }
        catch ( DocumentIOException e ) {
            throw new SimulationException( ERROR_PROPERTIES_TO_XML, e );
        }
        catch ( FileNotFoundException e ) {
            throw new SimulationException( ERROR_EXPORT_FILE_NOT_FOUND + " : " + file.getAbsolutePath(), e );
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
            throw new SimulationException( ERROR_CANNOT_OPEN_JAR + " : " + originalJarFileName, e );
        }
        
        File testFile = new File( newJarFileName );
        testFile.deleteOnExit(); // temporary file, delete when the VM exits
        try {
            // input comes from the original JAR file
            JarInputStream jarInputStream = new JarInputStream( inputStream ); // throws IOException
            Manifest manifest = jarInputStream.getManifest();
            if ( manifest == null ) {
                throw new SimulationException( ERROR_MISSING_MANIFEST + " : " + originalJarFileName );
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
            throw new SimulationException( ERROR_CANNOT_INSERT_PROPERTIES_FILE + " : " + newJarFileName, e );
        }
        catch ( DocumentIOException e ) {
            throw new SimulationException( ERROR_CANNOT_INSERT_PROPERTIES_FILE + " : " + newJarFileName, e );
        }
    }
}
