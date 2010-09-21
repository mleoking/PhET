
package edu.colorado.phet.common.phetcommon.statistics;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
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
 * This implementation posts an XML document to a PHP script, receives an XML response.
 * <p>
 * XML request format:
 * <code>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <submit_message>
 *   <statistics_message key=value ... />
 * </submit_message>
 * </code>
 * <p>
 * XML response format with no errors:
 * <code>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <submit_message_response status="true">
 *   <statistics_message_response status="true">
 *      <warning>message</warning>
 *   </statistics_message_response/>
 * </submit_message_response>
 * </code>
 * <p>
 * XML response format with errors:
 * <code>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <submit_message_response status="false">
 *   <statistics_message_response status="false">
 *      <warning>message</warning>
 *      <error>message</error>
 *   </statistics_message_response/>
 * </submit_message_response>
 * </code>
 */
public class StatisticsMessageSender {

    // XML tags and attributes
    private static final String ROOT_TAG = "submit_message";
    private static final String STATISTICS_MESSAGE_TAG = "statistics_message";
    private static final String SUCCESS_ATTRIBUTE = "success";
    private static final String ERROR_TAG = "error";
    private static final String WARNING_TAG = "warning";
    private static final String TRUE_VALUE = "true";

    private static final Logger LOGGER = LoggingUtils.getLogger( StatisticsMessageSender.class.getCanonicalName() );

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
            System.out.println( getClass().getName() + ": cannot connect, " + uhe.toString() );
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

        Element root = document.createElement( ROOT_TAG );
        document.appendChild( root );

        Element statisticsMessageElement = document.createElement( STATISTICS_MESSAGE_TAG );
        for ( int i = 0; i < message.getFieldCount(); i++ ) {
            statisticsMessageElement.setAttribute( message.getField( i ).getName(), message.getField( i ).getValue() );
        }
        root.appendChild( statisticsMessageElement );

        return document;
    }

    /*
     * Sends the message by posting it as an XML document.
     */
    private HttpURLConnection postDocument( Document document ) throws ParserConfigurationException, TransformerException, IOException {
        final String url = PhetCommonConstants.STATISTICS_SERVICE_URL;
        LOGGER.fine( "posting to url=" + url );
        LOGGER.fine( "query=" + XMLUtils.toString( document ));
        return XMLUtils.post( url, document );
    }

    /*
     * Parses the response to see if we succeeded.
     */
    private boolean parseResponse( Document document ) throws IOException, SAXException, ParserConfigurationException, TransformerException {

        LOGGER.fine( "response=" + XMLUtils.toString( document ) );

        // look for warnings
        NodeList warnings = document.getElementsByTagName( WARNING_TAG );
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
        NodeList errors = document.getElementsByTagName( ERROR_TAG );
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

        // determine success
        String elementName = getResponseTag( STATISTICS_MESSAGE_TAG );
        String attributeValue = getAttribute( document, elementName, SUCCESS_ATTRIBUTE );
        return attributeValue.equals( TRUE_VALUE );
    }

    /*
     * Convention for naming a response tag.
     * For example, request "foo" has response "foo_response".
     */
    private static String getResponseTag( String tag ) {
        return tag + "_response";
    }

    /*
     * Gets the first occurrence of an attribute.
     */
    private static String getAttribute( Document document, String elementName, String attributeName ) {
        String value = null;
        NodeList nodelist = document.getElementsByTagName( elementName );
        if ( nodelist.getLength() > 0 ) {
            Element element = (Element) nodelist.item( 0 );
            value = element.getAttribute( attributeName );
            if ( value != null && value.length() == 0 ) {
                value = null;
            }
        }
        return value;
    }

    public static void main( String[] args ) throws IOException {
        PhetApplicationConfig config = new PhetApplicationConfig( null, "balloons" );
        SessionCounter.initInstance( config.getProjectName(), config.getFlavor() );
        SessionMessage.initInstance( config );
        StatisticsMessageSender sender = new StatisticsMessageSender();
        boolean success = sender.sendMessage( SessionMessage.getInstance() );
        System.out.println( "success=" + success );
    }
}