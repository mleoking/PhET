/* Copyright 2008-2010, University of Colorado */

package edu.colorado.phet.buildtools.jar;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.colorado.phet.common.phetcommon.view.util.XMLUtils;

/**
 * Java sims store localized strings in properties files, while Flash sims used XML files.
 * This class converts between properties and the XML formats.
 * <p/>
 * For Flash's localized strings files:
 * The root element is <SimStrings>.
 * The element for each localized string is <string key="key" value="value" />.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FlashStringsAdapter {

    private static final String ENCODING = "UTF-8";

    private static final String ROOT_ELEMENT = "SimStrings";
    private static final String STRING_ELEMENT = "string";
    private static final String KEY_ATTRIBUTE = "key";
    private static final String VALUE_ATTRIBUTE = "value";

    /**
     * Converts an XML Document object to a Properties object.
     * The XML document is in the format required for Flash simulations.
     *
     * @param properties
     * @return Properties
     * @throws PropertiesFlashAdapterException
     *
     */
    public static final Properties documentToProperties( Document document ) {
        Properties properties = new Properties();
        NodeList elements = document.getElementsByTagName( STRING_ELEMENT );
        int numberOfNodes = elements.getLength();
        for ( int i = 0; i < numberOfNodes; i++ ) {
            Element element = (Element) elements.item( i );
            String key = element.getAttribute( KEY_ATTRIBUTE );
            String value = element.getAttribute( VALUE_ATTRIBUTE );
            properties.setProperty( key, value );
        }
        return properties;
    }

    /*
    * Converts a Properties object to an XML Document, in the format required for Flash simulations.
    *
    * @param properties
    * @param header comment that describes the contents of the document
    * @return Document
    * @throws PropertiesFlashAdapterException
    */
    private static final Document propertiesToDocument( Properties properties, String header ) throws ParserConfigurationException {
        // create a document
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        Document document = builder.newDocument();

        // Add the header as a comment
        Comment comment = document.createComment( header );
        document.appendChild( comment );

        // Insert the root element node
        Element rootElement = document.createElement( ROOT_ELEMENT );
        document.appendChild( rootElement );

        // add all key-value pairs to the document
        Enumeration<?> keys = properties.propertyNames();
        while ( keys.hasMoreElements() ) {
            String key = (String) keys.nextElement();
            String value = properties.getProperty( key );
            Element element = document.createElement( STRING_ELEMENT );
            element.setAttribute( KEY_ATTRIBUTE, key );
            element.setAttribute( VALUE_ATTRIBUTE, value );
            rootElement.appendChild( element );
        }

        return document;
    }

    /**
     * Reads a Properties object from an input stream.
     *
     * @param fileName
     * @return Properties
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws DocumentIOException
     */
    public static final Properties readProperties( InputStream inputStream ) throws ParserConfigurationException, IOException, SAXException {
        Document document = XMLUtils.readDocument( inputStream );
        return documentToProperties( document );
    }

    /**
     * Saves a Properties object to an XML file.
     *
     * @param properties
     * @param header     comment that describes the contents of the XML file
     * @param fileName
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws DocumentIOException
     */
    public static final void writeProperties( Properties properties, String header, OutputStream outputStream ) throws TransformerException, ParserConfigurationException {
        Document document = propertiesToDocument( properties, header );
        XMLUtils.writeDocument( document, outputStream, ENCODING );
    }

    /**
     * Test program.
     *
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws SAXException
     * @throws DocumentIOException
     */
    public static void main( String[] args ) throws IOException, TransformerException, ParserConfigurationException, SAXException {

        String tmpDir = System.getProperty( "java.io.tmpdir" );
        String fileSeparator = System.getProperty( "file.separator" );

        // Write the System properties to an XML file
        Properties properties = System.getProperties();
        String header = "this comment describes the contents of this XML file";
        String filename = tmpDir + fileSeparator + "test.xml";
        writeProperties( properties, header, new FileOutputStream( filename ) );

        // Read the XML file that we wrote above
        Properties inputProperties = readProperties( new FileInputStream( filename ) );

        // Sort & print key/value pairs
        Object[] keySet = inputProperties.keySet().toArray();
        List keys = Arrays.asList( keySet );
        Collections.sort( keys );
        for ( int i = 0; i < keys.size(); i++ ) {
            Object key = keys.get( i );
            Object value = inputProperties.get( key );
            System.out.println( key + ": " + value );
        }
    }
}
