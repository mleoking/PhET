package edu.colorado.phet.translationutility.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.Element;
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
        FileOutputStream oStream = new FileOutputStream( xmlFilename );
        outputter.output(  doc, oStream );
    }
    
    /**
     * Test program.
     */
    public static void main( String[] args ) {
        
        String tmpDir = System.getProperty( "java.io.tmpdir" );
        String fileSeparator = System.getProperty( "file.separator" );
        
        // Write the System properties to /tmp/test.xml
        Properties properties = System.getProperties();
        String tmpFile = tmpDir + fileSeparator + "test.xml";
        try {
            propertiesToXML( properties, tmpFile );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }
    
}
