/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.util;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * DocumentIO handles input/output of XML documents.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DocumentIO {

    /**
     * All exceptions encountered are recast as this exception type.
     */
    public static class DocumentIOException extends Exception {
        public DocumentIOException( String message ) {
            super( message );
        }
        public DocumentIOException( String message, Throwable cause ) {
            super( message, cause );
        }
    }
    
    /**
     * Reads an XML document from an input stream.
     * 
     * @param inputStream
     * @return Document
     * @throws DocumentIOException
     */
    public static final Document readDocument( InputStream inputStream ) throws DocumentIOException {
        Document document = null;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse( inputStream );
        }
        catch ( ParserConfigurationException e ) {
            throw new DocumentIOException( "failed to create XML document builder", e );
        }
        catch ( IOException e ) {
            throw new DocumentIOException( "error reading XML document", e );
        }
        catch ( SAXException e ) {
            throw new DocumentIOException( "error parsing XML document", e );
        }
        return document;
    }
    
    /**
     * Writes an XML document to an output stream.
     * 
     * @param document
     * @param outputStream
     * @throws DocumentIOException
     */
    public static void writeDocument( Document document, OutputStream outputStream, String encoding ) throws DocumentIOException {
        try {
            // write the document to a file
            Source source = new DOMSource( document );
            Result result = new StreamResult( outputStream );
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty( "indent", "yes" ); // make the output easier to read, see Transformer.getOutputProperties
            transformer.setOutputProperty( "encoding", encoding );
            transformer.transform( source, result );
        }
        catch ( TransformerException e ) {
            throw new DocumentIOException( "failed to write XML file", e );
        }
    }
}
