/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildtools.jar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.colorado.phet.common.phetcommon.view.util.XMLUtils;
import edu.colorado.phet.flashlauncher.util.SimulationProperties;

/**
 * Utility methods related to JAR files.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JarUtils {

    /* not intended for instantiation */
    private JarUtils() {
    }

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
     * Does a JAR contain a specified file?
     *
     * @param jarFileName
     * @param fileName
     * @return true or false
     * @throws IOException
     */
    public static boolean containsFile( String jarFileName, String fileName ) throws IOException {

        // open the jar file
        JarInputStream jarInputStream = openJar( jarFileName );

        // look for the specified file
        JarEntry jarEntry = getJarEntry( jarInputStream, fileName );

        // close the jar file
        jarInputStream.close();

        return ( jarEntry != null );
    }

    /**
     * Reads a properties file from a jar.
     *
     * @param jarFileName
     * @param fileName
     * @return Properties
     * @throws IOException
     */
    public static Properties readProperties( String jarFileName, String fileName ) throws IOException, FileNotFoundException {

        // open the jar file
        JarInputStream jarInputStream = openJar( jarFileName );

        // look for the properties file
        JarEntry jarEntry = getJarEntry( jarInputStream, fileName );

        // read the properties file into a Properties object
        Properties properties = null;
        if ( jarEntry != null ) {
            properties = new Properties();
            properties.load( jarInputStream );
        }
        else {
            throw new FileNotFoundException( "file not found: " + fileName );
        }

        // close the jar file
        jarInputStream.close();

        return properties;
    }

    /**
     * Reads SimulationProperties from a jar.
     *
     * @param jarFileName
     * @return SimulationProperties
     * @throws IOException
     */
    public static SimulationProperties readSimulationProperties( String jarFileName ) throws IOException {
        return new SimulationProperties( JarUtils.readProperties( jarFileName, SimulationProperties.FILENAME ) );
    }

    /**
     * Reads a text file from a jar.
     *
     * @param jarFileName
     * @param fileName
     * @param bufferSize  size of the buffer used for reading
     * @return String
     * @throws IOException
     */
    public static String readText( String jarFileName, String fileName, int bufferSize ) throws IOException, FileNotFoundException {

        // open the jar file
        JarInputStream jarInputStream = openJar( jarFileName );

        // look for the text file
        JarEntry jarEntry = getJarEntry( jarInputStream, fileName );

        // read the file's contents into a String buffer
        StringBuffer stringBuffer = null;
        if ( jarEntry != null ) {
            stringBuffer = new StringBuffer( bufferSize );
            BufferedReader reader = new BufferedReader( new InputStreamReader( jarInputStream ) );
            char[] buffer = new char[bufferSize];
            int numRead = 0;
            while ( ( numRead = reader.read( buffer ) ) != -1 ) {
                stringBuffer.append( buffer, 0, numRead );
            }
        }
        else {
            throw new FileNotFoundException( "file not found: " + fileName );
        }

        // close the jar file
        jarInputStream.close();

        return stringBuffer.toString();
    }

    /**
     * Reads an XML document from the specified JAR file.
     */
    public static Document readDocument( String jarFileName, String fileName ) throws ParserConfigurationException, IOException, SAXException, FileNotFoundException {

        // open the jar file
        JarInputStream jarInputStream = openJar( jarFileName );

        // find the XML file
        JarEntry jarEntry = getJarEntry( jarInputStream, fileName );

        // read the document
        Document document = null;
        if ( jarEntry != null ) {
            document = XMLUtils.readDocument( jarInputStream );
        }
        else {
            throw new FileNotFoundException( "file not found: " + fileName );
        }

        // close the jar
        jarInputStream.close();

        return document;
    }

    /*
    * Positions the stream at the beginning of a specific entry.
    * Returns null if the entry is not found.
    */
    private static JarEntry getJarEntry( JarInputStream jarInputStream, String name ) throws IOException {
        JarEntry jarEntry = jarInputStream.getNextJarEntry();
        while ( jarEntry != null ) {
            if ( jarEntry.getName().equals( name ) ) {
                break;
            }
            else {
                jarEntry = jarInputStream.getNextJarEntry();
            }
        }
        return jarEntry;
    }

    /**
     * Creates the proper type of jar file interface, based on info in the input jar.
     *
     * @param inputJarName
     * @return JarCreator
     * @throws IOException
     */
    public static JarCreator getJarCreator( String inputJarName ) throws IOException {
        JarCreator creator = null;
        SimulationProperties properties = JarUtils.readSimulationProperties( inputJarName );
        if ( properties.isFlash() ) {
            creator = new FlashJarCreator();
        }
        else {
            creator = new JavaJarCreator();
        }
        return creator;
    }
}
