
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
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
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
            Document messageDocument = toDocument( message );
            HttpURLConnection connection = postDocument( messageDocument );
            Document responseDocument = XMLUtils.readDocument( connection );
            success = parseResponse( responseDocument );
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
    private static Document toDocument( StatisticsMessage message ) throws ParserConfigurationException, TransformerException {

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
     * Sends the message by posting it as an XML document.
     */
    private HttpURLConnection postDocument( Document document ) throws ParserConfigurationException, TransformerException, IOException {
        final String url = PhetCommonConstants.STATISTICS_SERVICE_URL;
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( getClass().getName() + " posting to url=" + url );
            System.out.println( getClass().getName() + " query=\n" + XMLUtils.toString( document ) );
        }
        return XMLUtils.post( url, document );
    }

    /*
     * Parses the response to see if we succeeded.
     */
    private boolean parseResponse( Document responseDocument ) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        
        boolean success = false;
        
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( getClass().getName() + " response=\n" + XMLUtils.toString( responseDocument ) );
        }
        
        // look for warnings
        NodeList warnings = responseDocument.getElementsByTagName( "warning-message" );
        for ( int i = 0; i < warnings.getLength(); i++ ) {
            Element element = (Element) warnings.item( i );
            NodeList children = element.getChildNodes();
            for ( int j = 0; j < children.getLength(); j++ ) {
                if ( children.item( j ) instanceof Text ) {
                    Text text = (Text) children.item( j );
                    System.out.println( "WARNING: " + StatisticsMessageSender.class.getName() + ": " + text.getData() );
                }
            }
        }
        
        // look for errors
        NodeList errors = responseDocument.getElementsByTagName( "error-message" );
        for ( int i = 0; i < errors.getLength(); i++ ) {
            Element element = (Element) errors.item( i );
            NodeList children = element.getChildNodes();
            for ( int j = 0; j < children.getLength(); j++ ) {
                if ( children.item( j ) instanceof Text ) {
                    Text text = (Text) children.item( j );
                    System.err.println( "ERROR: " + StatisticsMessageSender.class.getName() + ": " + text.getData() );
                }
            }
        }
        
        // success is based on errors
        success = ( errors.getLength() == 0 );

        return success;
    }


    public static void main( String[] args ) throws IOException {
        PhetApplicationConfig config = new PhetApplicationConfig( null, "balloons" );
        SessionCounter.initInstance( config.getProjectName(), config.getFlavor() );
        SessionMessage.initInstance( config );
        new StatisticsMessageSender().sendMessage( SessionMessage.getInstance() );
    }
}