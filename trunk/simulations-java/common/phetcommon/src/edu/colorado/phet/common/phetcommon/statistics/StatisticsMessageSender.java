
package edu.colorado.phet.common.phetcommon.statistics;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.SessionCounter;
import edu.colorado.phet.common.phetcommon.view.util.XMLUtils;

/**
 * Sends a statistics message to PhET.
 * This implementation posts an XML document to a PHP script.
 */
public class StatisticsMessageSender {

    // prints debug output to the System.out
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
            Document document = toDocument( message );
            success = postDocument( PhetCommonConstants.STATISTICS_SERVICE_URL, document );
        }
        catch ( UnknownHostException uhe ) {
            System.err.println( getClass().getName() + " could not send message, perhaps network is unavailable: " + uhe.toString() );
        }
        catch ( ParserConfigurationException e ) {
            e.printStackTrace();
        }
        catch ( TransformerException e ) {
            e.printStackTrace();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        catch ( SAXException e ) {
            e.printStackTrace();
        }
        return success;
    }

    /*
     * Converts a message to an XML Document.
     */
    private Document toDocument( StatisticsMessage message ) throws ParserConfigurationException, TransformerException {

        //see: http://www.genedavis.com/library/xml/java_dom_xml_creation.jsp
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement( "statistics" );
        for ( int i = 0; i < message.getFieldCount(); i++ ) {
            root.setAttribute( message.getField( i ).getName(), message.getField( i ).getValue() );
        }
        document.appendChild( root );

        return document;
    }

    /*
     * Posts an XML document to the specified URL.
     * Processes the result.
     */
    private static boolean postDocument( String url, Document queryDocument ) throws IOException, TransformerException, SAXException, ParserConfigurationException {

        boolean success = false;

        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( StatisticsMessageSender.class.getName() + " posting to url=" + url );
            System.out.println( StatisticsMessageSender.class.getName() + " query=" + XMLUtils.toString( queryDocument ) );
        }

        // post Document
        HttpURLConnection connection = XMLUtils.post( url, queryDocument );

        // read response
        Document responseDocument = XMLUtils.readDocument( connection );

        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( StatisticsMessageSender.class.getName() + ": response=" + XMLUtils.toString( responseDocument ) );
        }

        success = true; //TODO #1286, parse the response, set success correctly, print errors and warnings to System.err

        return success;
    }


    public static void main( String[] args ) throws IOException {
        PhetApplicationConfig config = new PhetApplicationConfig( null, "balloons" );
        SessionCounter.initInstance( config.getProjectName(), config.getFlavor() );
        SessionMessage.initInstance( config );
        new StatisticsMessageSender().sendMessage( SessionMessage.getInstance() );
    }
}