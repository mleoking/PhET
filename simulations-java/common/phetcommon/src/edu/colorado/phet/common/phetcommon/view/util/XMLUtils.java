package edu.colorado.phet.common.phetcommon.view.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class XMLUtils {
    
    /* not intended for instantiation */
    private XMLUtils() {}

    /**
     * Converts a Document to a String.
     * @param document
     * @return
     * @throws TransformerException
     */
    public static String toString( Document document ) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer trans = transformerFactory.newTransformer();
        trans.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
        trans.setOutputProperty( OutputKeys.INDENT, "yes" );

        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult( stringWriter );
        DOMSource source = new DOMSource( document );
        trans.transform( source, result );
        return stringWriter.toString();
    }
    
    /**
     * Uses http to post a Document to a URL.
     * @param url
     * @param document
     * @return 
     * @throws IOException
     * @throws UnknownHostException
     * @throws TransformerException
     */
    public static HttpURLConnection post( String url, Document document ) throws IOException, UnknownHostException, TransformerException {
        return post( url, toString( document ) );
    }
    
    /**
     * Uses http to post an XML String to a URL.
     * @param url
     * @param xmlString
     * @param return
     * @throws IOException
     * @throws UnknownHostException
     */
    public static HttpURLConnection post( String url, String xmlString ) throws IOException, UnknownHostException {

        // open connection
        URL urlObject = new URL( url );
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod( "POST" );
        connection.setRequestProperty( "Content-Type", "text/xml; charset=\"utf-8\"" );
        connection.setDoOutput( true );

        OutputStreamWriter outStream = new OutputStreamWriter( connection.getOutputStream(), "UTF-8" );
        outStream.write( xmlString );
        outStream.close();
       
        return connection;
    }
    
    /**
     * Reads a Document from an HttpURLConnection.
     * @param connection
     * @return
     * @throws IOException
     * @throws ParserConfigurationException 
     * @throws SAXException 
     */
    public static Document readDocument( HttpURLConnection connection ) throws IOException, SAXException, ParserConfigurationException {
        String xmlString = readString( connection );
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder().parse( xmlString );
    }
    
    /**
     * Reads an XML String from an HttpURLConnection.
     * @param connection
     * @return
     * @throws IOException
     */
    public static String readString( HttpURLConnection connection ) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
        String line;
        while ( ( line = reader.readLine() ) != null ) {
            buffer.append( line );
        }
        reader.close();
        return buffer.toString();
    }
}
