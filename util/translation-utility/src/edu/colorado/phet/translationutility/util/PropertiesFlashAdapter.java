/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.util;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Converts between Java Properties and the format required for Flash sims.
 * Flash sims require an XML file.
 * The root element is <SimStrings>.
 * The element for each localized string is <string key="key" value="value" />.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PropertiesFlashAdapter {

    private static final String ENCODING = "UTF-8";
    
    private static final String ROOT_ELEMENT = "SimStrings";
    private static final String STRING_ELEMENT = "string";
    private static final String KEY_ATTRIBUTE = "key";
    private static final String VALUE_ATTRIBUTE = "value";
    
    /**
     * All exceptions encountered are recast as this exception type.
     */
    public static class PropertiesFlashAdapterException extends Exception {
        public PropertiesFlashAdapterException( String message ) {
            super( message );
        }
        public PropertiesFlashAdapterException( String message, Throwable cause ) {
            super( message, cause );
        }
    }
    
    /**
     * Saves a Properties object to an XML file.
     * 
     * @param properties
     * @param xmlFilename
     * @throws IOException
     * @throws PropertiesFlashAdapterException 
     */
    public static final void propertiesToXML( Properties properties, String xmlFilename ) throws PropertiesFlashAdapterException {
        
        try {
            // create a document
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();

            // Insert the root element node
            Element rootElement = document.createElement( ROOT_ELEMENT );
            document.appendChild( rootElement );

            // add all key-value pairs to the document
            Enumeration keys = properties.propertyNames();
            while ( keys.hasMoreElements() ) {
                String key = (String) keys.nextElement();
                String value = properties.getProperty( key );
                Element element = document.createElement( STRING_ELEMENT );
                element.setAttribute( KEY_ATTRIBUTE, key );
                element.setAttribute( VALUE_ATTRIBUTE, value );
                rootElement.appendChild( element );
            }

            // write the document to a file
            Source source = new DOMSource( document );
            File file = new File( xmlFilename );
            Result result = new StreamResult( file );
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty( "indent", "yes" ); // make the output easier to read, see Transformer.getOutputProperties
            transformer.setOutputProperty( "encoding", ENCODING );
            transformer.transform( source, result );
        }
        catch ( ParserConfigurationException e ) {
            throw new PropertiesFlashAdapterException( "failed to create XML document builder", e );
        }
        catch ( TransformerException e ) {
            throw new PropertiesFlashAdapterException( "failed to write XML file", e );
        }
    }
    
    /**
     * Reads a Properties object from an XML file.
     * 
     * @param xmlFilename
     * @return Properties null if the file wasn't read or didn't contain any <string> elements.
     * @throws PropertiesFlashAdapterException
     */
    public static final Properties xmlToProperties( String xmlFilename ) throws PropertiesFlashAdapterException {

        Properties properties = null;
        try {
            // Read the file into a document
            File file = new File( xmlFilename );
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse( file );
            NodeList elements = document.getElementsByTagName( STRING_ELEMENT );

            // Parse the document
            int numberOfNodes = elements.getLength();
            if ( numberOfNodes > 0 ) {
                properties = new Properties();
                for ( int i = 0; i < numberOfNodes; i++ ) {
                    Element element = (Element) elements.item( i );
                    String key = element.getAttribute( KEY_ATTRIBUTE );
                    String value = element.getAttribute( VALUE_ATTRIBUTE );
                    properties.setProperty( key, value );
                }
            }
        }
        catch ( ParserConfigurationException e ) {
            throw new PropertiesFlashAdapterException( "failed to create XML document builder", e );
        }
        catch ( IOException e ) {
            throw new PropertiesFlashAdapterException( "error reading XML document", e );
        }
        catch ( SAXException e ) {
            throw new PropertiesFlashAdapterException( "error parsing XML document", e );
        }
        
        return properties;
    }
    
    /**
     * Test program.
     * @throws PropertiesFlashAdapterException 
     */
    public static void main( String[] args ) throws PropertiesFlashAdapterException {

        String tmpDir = System.getProperty( "java.io.tmpdir" );
        String fileSeparator = System.getProperty( "file.separator" );

        // Write the System properties to an XML file
        Properties properties = System.getProperties();
        String xmlFilename = tmpDir + fileSeparator + "test.xml";
        propertiesToXML( properties, xmlFilename );

        // Read the XML file that we wrote above
        Properties inputProperties = xmlToProperties( xmlFilename );

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
