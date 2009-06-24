package edu.colorado.phet.tomcattest.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

public class XMLUtils {
    private XMLUtils() {
    }

    /**
     * Converts a Document to an XML String.
     *
     * @param document The document to convert
     * @return The corresponding String
     * @throws javax.xml.transform.TransformerException
     *
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
     *
     * @param string
     * @return
     * @throws TransformerException
     * @throws javax.xml.parsers.ParserConfigurationException
     *
     */
    public static Document toDocument( String string ) throws TransformerException, ParserConfigurationException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // StringReader source
        Source source = new StreamSource( new StringReader( string ) );

        // Document result
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Result result = new DOMResult( document );

        transformer.transform( source, result );
        return document;
    }
}
