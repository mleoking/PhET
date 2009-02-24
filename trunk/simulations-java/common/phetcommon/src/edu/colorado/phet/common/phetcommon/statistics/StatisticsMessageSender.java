package edu.colorado.phet.common.phetcommon.statistics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.SessionCounter;
import edu.colorado.phet.common.phetcommon.view.util.XMLUtils;

/**
 * Sends a statistics message to PhET.
 * This implementation posts an XML document to a PHP script.
 */
public class StatisticsMessageSender {

    private static final boolean ENABLE_DEBUG_OUTPUT = true;

    /**
     * Sends a statistics message to PhET.
     *
     * @param message
     * @return true if the message was successfully sent
     */
    public boolean sendMessage( StatisticsMessage message ) {
        boolean success = false;
        try {
            success = postXML( PhetCommonConstants.STATISTICS_SERVICE_URL, toXMLString( message ) );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        catch ( ParserConfigurationException e ) {
            e.printStackTrace();
        }
        catch ( TransformerException e ) {
            e.printStackTrace();
        }
        return success;
    }

    private String toXMLString( StatisticsMessage message ) throws ParserConfigurationException, TransformerException {

        //see: http://www.genedavis.com/library/xml/java_dom_xml_creation.jsp
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = doc.createElement( "statistics" );
        for (int i=0;i<message.getFieldCount();i++){
            root.setAttribute( message.getField(i ).getName(),message.getField( i ).getValue() );
        }
        doc.appendChild( root );
        
        return XMLUtils.toString( doc );
    }

    private static boolean postXML( String url, String xmlString ) throws IOException {
        
        // post
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( StatisticsMessageSender.class.getName() + ": posting to url=" + url + " xml=" + xmlString );
        }
        
        boolean success = false;
        
        // open connection
        URL urlObject = new URL( url );
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod( "POST" );
        connection.setRequestProperty( "Content-Type", "text/xml; charset=\"utf-8\"" );
        connection.setDoOutput( true );

        try {
            OutputStreamWriter outStream = new OutputStreamWriter( connection.getOutputStream(), "UTF-8" );
            outStream.write( xmlString );
            outStream.close();

            // Get the response
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            String line;
            while ( ( line = reader.readLine() ) != null ) {
                buffer.append( line );
            }
            reader.close();
            success = true;

            if ( ENABLE_DEBUG_OUTPUT ) {
                System.out.println( StatisticsMessageSender.class.getName() + ": response=" + buffer.toString() );
            }
            
            //TODO #1286, parse the response, set success correctly, print errors and warnings to System.err
        }
        catch( UnknownHostException uhe ) {
            System.err.println( StatisticsMessageSender.class.getName() + ": Could not sumbit message, perhaps network is unavailable: " + uhe.toString() );
        }
        return success;
    }


    public static void main( String[] args ) throws IOException {
        
        // send a bogus message
        String URL_STRING = PhetCommonConstants.STATISTICS_SERVICE_URL;
        String XML_STRING = "<xml>hello stats 1234</xml>";
        postXML( URL_STRING, XML_STRING );
        
        // send a valid session message
        PhetApplicationConfig config = new PhetApplicationConfig( null, "balloons");
        SessionCounter.initInstance( config.getProjectName(), config.getFlavor() );
        SessionMessage.initInstance( config );
        new StatisticsMessageSender().sendMessage( SessionMessage.getInstance() );
    }
}