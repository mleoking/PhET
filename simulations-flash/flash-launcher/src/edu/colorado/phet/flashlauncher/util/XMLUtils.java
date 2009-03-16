package edu.colorado.phet.flashlauncher.util;

import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringWriter;
import java.io.StringReader;

public class XMLUtils {
    private XMLUtils() {}

    /**
     * Converts a Document to an XML String.
     * @param document
     * @return
     * @throws javax.xml.transform.TransformerException
     */
    public static String toString( Document document ) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Document source
        DOMSource source = new DOMSource( document );

        // StringWriter result
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult( stringWriter );

        transformer.transform( source, result );
        return stringWriter.toString();
    }

    /**
     * Converts an XML String to a Document.
     * @param string
     * @return
     * @throws TransformerException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    public static Document toDocument( String string ) throws TransformerException, ParserConfigurationException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // StringReader source
        Source source = new StreamSource( new StringReader( string ) );

        // Document result
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Result result = new DOMResult(document);

        transformer.transform( source, result );
        return document;
    }

    public static String HTMLEntityEncode( String s ) {
        StringBuffer buf = new StringBuffer();
        int len = (s == null ? -1 : s.length());

        for( int i = 0; i < len; i++ ) {
            char c = s.charAt( i );
            if( c>='a' && c<='z' || c>='A' && c<='Z' || c>='0' && c<='9' ) {
                buf.append( c );
            } else {
                buf.append( "&#" + (int)c + ";" );
            }
        }
        return buf.toString();
    }
}
