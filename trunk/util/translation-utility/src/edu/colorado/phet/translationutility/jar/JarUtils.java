/* Copyright 2010, University of Colorado */

package edu.colorado.phet.translationutility.jar;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import edu.colorado.phet.flashlauncher.util.SimulationProperties;
import edu.colorado.phet.translationutility.jar.JarCreator.FlashJarCreator;
import edu.colorado.phet.translationutility.jar.JarCreator.JavaJarCreator;

/**
 * Utility methods related to JAR files.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JarUtils {

    /* not intended for instantiation */
    private JarUtils() {}
    
    /**
     * Opens a jar.
     * 
     * @param jarFileName
     * @return JarInputStream
     * @throws IOException
     */
    public static JarInputStream openJar( String jarFileName ) throws IOException {
        return new JarInputStream( new FileInputStream( jarFileName ) );
    }
    
    /**
     * Does a JAR contain a specified file?
     * 
     * @param jarFileName
     * @param fileName
     * @return true or false
     * @throws IOException
     */
    public static boolean containsFile( String jarFileName, String fileName ) throws IOException {
        
        boolean found = false;
        
        // open the jar file
        JarInputStream jarInputStream = openJar( jarFileName );
        
        // look for the specified file
        JarEntry jarEntry = jarInputStream.getNextJarEntry();
        while ( jarEntry != null ) {
            if ( jarEntry.getName().equals( fileName ) ) {
                found = true;
                break;
            }
            else {
                jarEntry = jarInputStream.getNextJarEntry();
            }
        }
        
        // close the jar file
        jarInputStream.close();

        return found;
    }
    
    /**
     * Reads a properties file from a jar.
     * 
     * @param jarFileName
     * @param fileName
     * @return Properties
     * @throws IOException
     */
    public static Properties readProperties( String jarFileName, String fileName ) throws IOException {
        
        // open the jar file
        JarInputStream jarInputStream = openJar( jarFileName );

        // look for the properties file
        boolean found = false;
        JarEntry jarEntry = jarInputStream.getNextJarEntry();
        while ( jarEntry != null ) {
            if ( jarEntry.getName().equals( fileName ) ) {
                found = true;
                break;
            }
            else {
                jarEntry = jarInputStream.getNextJarEntry();
            }
        }
        
        // read the properties file into a Properties object
        Properties properties = null;
        if ( found ) {
            properties = new Properties();
            properties.load( jarInputStream );
        }
        else {
            throw new IOException( "file not found: " + fileName );
        }

        // close the jar file
        jarInputStream.close();

        return properties;
    }
    
    /**
     * Reads SimulationProperties from a jar.
     * 
     * @param jarFileName
     * @param locale
     * @return SimulationProperties
     * @throws IOException
     */
    public static SimulationProperties readSimulationProperties( String jarFileName ) throws IOException {
        return SimulationPropertiesFactory.createSimulationProperties( jarFileName );
    }
    
    /**
     * Reads a text file from a jar, using a default buffer size.
     * 
     * @param jarFileName
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readText( String jarFileName, String fileName ) throws IOException {
        return readText( jarFileName, fileName, 1024 );
    }
    
    /**
     * Reads a text file from a jar.
     * 
     * @param jarFileName
     * @param fileName
     * @param bufferSize size of the buffer used for reading
     * @return String
     * @throws IOException
     */
    public static String readText( String jarFileName, String fileName, int bufferSize ) throws IOException {
        
        // open the jar file
        JarInputStream jarInputStream = openJar( jarFileName );

        // look for the text file
        boolean found = false;
        JarEntry jarEntry = jarInputStream.getNextJarEntry();
        while ( jarEntry != null ) {
            if ( jarEntry.getName().equals( fileName ) ) {
                found = true;
                break;
            }
            else {
                jarEntry = jarInputStream.getNextJarEntry();
            }
        }
        
        // read the file's contents into a String buffer
        StringBuffer stringBuffer = null;
        if ( found ) {
            stringBuffer = new StringBuffer( bufferSize );
            BufferedReader reader = new BufferedReader( new InputStreamReader( jarInputStream ) );
            char[] buffer = new char[bufferSize];
            int numRead = 0;
            while ( ( numRead = reader.read( buffer ) ) != -1 ) {
                stringBuffer.append( buffer, 0, numRead );
            }
        }
        else {
            throw new IOException( "file not found: " + fileName );
        }

        // close the jar file
        jarInputStream.close();
        
        return stringBuffer.toString();
    }

    /**
     * Gets the manifest from a jar file.
     * All JAR files for PhET simulations must have a manifest.
     * 
     * @param jarFileName
     * @return Manifest
     * @throws IOException
     */
    public static Manifest getManifest( String jarFileName ) throws IOException {
        JarFile jarFile = new JarFile( jarFileName );
        JarEntry jarEntry = jarFile.getJarEntry( JarFile.MANIFEST_NAME );
        InputStream inputStream = jarFile.getInputStream( jarEntry );
        Manifest manifest = new Manifest( inputStream ); // constructor reads the input stream
        inputStream.close();
        jarFile.close();
        return manifest;
    }
    
    /**
     * Reads an XML document from the specified JAR file, and converts it to Properties.
     * The XML document contains localized strings.
     */
    public static Properties readXMLAsProperties( String jarFileName, String xmlFilename ) throws IOException, DocumentIO.DocumentIOException {
        
        // open the jar file
        JarInputStream jarInputStream = openJar( jarFileName );
        
        // find the XML file
        boolean found = false;
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
        
        // convert the XML to Properties
        Properties properties = new Properties();
        if ( found ) {
            properties = DocumentAdapter.readProperties( jarInputStream );
        }
        else {
            throw new IOException( "file not found: " + xmlFilename );
        }

        // close the jar
        jarInputStream.close();

        return properties;
    }
    
    /**
     * Creates a localized jar file of the proper type, based on info in the input jar file.
     */
    public static void createLocalizedJar( String inputJarName, String outputJarName, Locale locale, Properties localizedStrings, String stringResourcePath, boolean deleteOnExit ) throws IOException {
        SimulationProperties properties = JarUtils.readSimulationProperties( inputJarName );
        if ( properties.isFlash() ) {
            new FlashJarCreator().createLocalizedJar( inputJarName, outputJarName, locale, localizedStrings, stringResourcePath, deleteOnExit );
        }
        else {
            new JavaJarCreator().createLocalizedJar( inputJarName, outputJarName, locale, localizedStrings, stringResourcePath, deleteOnExit );
        }
    }
}
