package edu.colorado.phet.translationutility.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Converts between Java Properties and the format required for Flash sims.
 * Flash sims require an XML file.
 * The root element is <SimStrings>.
 * The element for each key-value pair is <string key="key" value="value" />.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PropertiesFlashAdapter {

    private static final String STRING_TAG = "string";
    private static final String KEY_ATTRIBUTE = "key";
    private static final String VALUE_ATTRIBUTE = "value";
    
    /**
     * Saves Properties to an XML file.
     * 
     * @param properties
     * @param xmlFilename
     * @throws IOException
     */
    public static final void propertiesToXML( Properties properties, String xmlFilename ) throws IOException {
        
        Element root = new Element( "SimStrings" );
        Document doc = new Document( root );
        
        Enumeration keys = properties.propertyNames();
        while ( keys.hasMoreElements() ) {
            String key = (String) keys.nextElement();
            String value = properties.getProperty( key );
            Element element = new Element( STRING_TAG );
            element.setAttribute( KEY_ATTRIBUTE, key );
            element.setAttribute( VALUE_ATTRIBUTE, value );
            root.addContent( element );
        }
        
        XMLOutputter outputter = new XMLOutputter( " ", true );
        FileOutputStream outputStream = new FileOutputStream( xmlFilename );
        outputter.output(  doc, outputStream );
    }
    
    /**
     * Reads Properties from an XML file.
     * 
     * @param xmlFilename
     * @return Properties
     * @throws IOException
     * @throws JDOMException
     */
    public static final Properties xmlToProperties( String xmlFilename ) throws IOException, JDOMException {
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream( xmlFilename );
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build( inputStream );
        List elements = doc.getRootElement().getChildren();
        Iterator i = elements.iterator();
        while ( i.hasNext() ) {
            Element element = (Element) i.next();
            String name = element.getName();
            if ( STRING_TAG.equals( name ) ) {
                String key = element.getAttribute( KEY_ATTRIBUTE ).getValue();
                String value = element.getAttribute( VALUE_ATTRIBUTE ).getValue();
                properties.setProperty( key, value );
            }
        }
        return properties;
    }
    
    /**
     * Test program.
     */
    public static void main( String[] args ) {
        
        String tmpDir = System.getProperty( "java.io.tmpdir" );
        String fileSeparator = System.getProperty( "file.separator" );
        
        // Write the System properties to /tmp/test.xml
        Properties properties = System.getProperties();
        String xmlFilename = tmpDir + fileSeparator + "test.xml";
        try {
            propertiesToXML( properties, xmlFilename );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        
        // Read the XML file we wrote above
        try {
            Properties inputProperties = xmlToProperties( xmlFilename );
            
            // Sort
            Object[] keySet = inputProperties.keySet().toArray();
            List keys = Arrays.asList( keySet );
            Collections.sort( keys );

            // Print out each key/value pair
            for( int i = 0; i < keys.size(); i++ ) {
                Object key = keys.get( i );
                Object value = inputProperties.get( key );
                System.out.println( key + ": " + value );
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        catch ( JDOMException e ) {
            e.printStackTrace();
        }
    }
    
}
