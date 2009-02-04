package edu.colorado.phet.common.phetcommon.statistics;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.SessionCounter;

/**
 * Statistics service that uses PHP to deliver a statistics message to PhET.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class XMLStatisticsService implements IStatisticsService {

    private static final boolean ENABLE_DEBUG_OUTPUT = true;

    /**
     * Delivers a statistics message to PhET.
     *
     * @param message
     * @return true if the message was successfully posted
     */
    public boolean postMessage( StatisticsMessage message ) throws IOException {
        return postXML( getStatisticsURL( message ), toXML( message ) );
    }

    private String toXML( StatisticsMessage message ) {
        try {
            return toXMLFromDom( message );
        }
        catch( Exception e ) {
            e.printStackTrace();
            return toXMLFromStringConcatenation( message );
        }
    }

    private String toXMLFromDom( StatisticsMessage message ) throws ParserConfigurationException, TransformerException {

        //see: http://www.genedavis.com/library/xml/java_dom_xml_creation.jsp
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = doc.createElement( "statistics" );
        for (int i=0;i<message.getFieldCount();i++){
            root.setAttribute( message.getField(i ).getName(),message.getField( i ).getValue() );
        }
        doc.appendChild( root );

        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
        trans.setOutputProperty( OutputKeys.INDENT, "yes" );

        //create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult( sw );
        DOMSource source = new DOMSource( doc );
        trans.transform( source, result );
        String xmlString = sw.toString();

        //print xml
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( getClass().getName() + ": xmlString=\n" + xmlString );
        }
        return xmlString;
    }

    private String toXMLFromStringConcatenation( StatisticsMessage message ) {
        String xml = "<statistics ";
        for ( int i = 0; i < message.getFieldCount(); i++ ) {
            xml += message.getField( i ).getName() + "=\"" + encode( message.getField( i ).getValue() + "\"" ) + " ";
        }
        xml += "/>";
        return xml;
    }

    private String encode( String value ) {
        //TODO: turn - into %2D as in flash?
        return value;
    }

    /*
     * The URL points to a PHP script, with name/value pairs appended to the URL.
     */
    private String getStatisticsURL( StatisticsMessage message ) {
        return "http://phet.colorado.edu/statistics/submit_message.php";
    }

    private static boolean postXML( String url, String xml ) throws IOException {
        
        boolean success = false;
        
        // open connection
        URL urlObject = new URL( url );
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod( "POST" );
        connection.setRequestProperty( "Content-Type", "text/xml; charset=\"utf-8\"" );
        connection.setDoOutput( true );

        // post
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( XMLStatisticsService.class.getName() + ": posting to url=" + url + " xml=" + xml );
        }
        try {
            OutputStreamWriter outStream = new OutputStreamWriter( connection.getOutputStream(), "UTF-8" );
            outStream.write( xml );
            outStream.close();

            // Get the response
            if ( ENABLE_DEBUG_OUTPUT ) {
                BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
                System.out.println( XMLStatisticsService.class.getName() + ": reading response ..." );
                String line;
                while ( ( line = reader.readLine() ) != null ) {
                    System.out.println( line );
                }
                reader.close();
                success = true;
                System.out.println( "done." );
            }
        }
        catch( UnknownHostException uhe ) {
            System.err.println( XMLStatisticsService.class.getName() + ": Could not sumbit message, perhaps network is unavailable: " + uhe.toString() );
        }
        return success;
    }


    public static void main( String[] args ) throws IOException {
        
        // send a bogus message
        String URL_STRING = "http://phet.colorado.edu/statistics/submit_message.php";
        String XML_STRING = "<xml>hello stats 1234</xml>";
        postXML( URL_STRING, XML_STRING );
        
        // send a valid session message
        PhetApplicationConfig config = new PhetApplicationConfig( null, "balloons");
        SessionCounter.initInstance( config.getProjectName(), config.getFlavor() );
        SessionMessage.initInstance( config );
        new XMLStatisticsService().postMessage( SessionMessage.getInstance() );
    }
}