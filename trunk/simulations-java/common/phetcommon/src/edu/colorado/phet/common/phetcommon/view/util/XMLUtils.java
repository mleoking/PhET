package edu.colorado.phet.common.phetcommon.view.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class XMLUtils {
    
    /* not intended for instantiation */
    private XMLUtils() {}

    /**
     * Converts a Document to an XML String.
     * @param document
     * @return
     * @throws TransformerException
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
     * Converts an XML String to a Document
     * @param string
     * @return
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    public static Document toDocument( String string ) throws TransformerException, ParserConfigurationException {

        //see http://www.exampledepot.com/egs/javax.xml.parsers/BasicDom.html
        try {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Create the builder and parse the file
            Document doc = factory.newDocumentBuilder().parse( new ByteArrayInputStream( string.getBytes() ) );
            return doc;
        }
        catch( SAXException e ) {
            // A parsing error occurred; the xml input is not valid
            e.printStackTrace(  );
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace(  );
        }
        catch( IOException e ) {
            e.printStackTrace(  );
        }
        return null;
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
     * @throws TransformerException 
     */
    public static Document readDocument( HttpURLConnection connection ) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        String xmlString = readString( connection );
        return toDocument( xmlString );
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
