
package edu.colorado.phet.common.phetcommon.application;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.resources.PhetInstallerVersion;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.view.util.XMLUtils;

/**
 * Query that returns version information about the simulation and PhET installer.
 * <p>
 * XML query format:
 * <code>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <php_info>
 *   <sim_version project="faraday" sim="magnet-and-compass" requested_by="automatic"/>
 *   <phet_installer_update timestamp_seconds="1234567890" requested_by="automatic"/>
 * </php_info>
 * </code>
 * <p>
 * XML response format with no errors:
 * <code>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <php_info_response>
 *   <sim_version_response
 *      version_major="1" 
 *      version_minor="01" 
 *      version_dev="00"
 *      version_revision="23478"
 *      version_timestamp="1234567890"
 *      ask_me_later_duration_days="1"/>
 *   <phet_installer_update_response
 *      recommend_update="false" 
 *      timestamp_seconds="1234567890"
 *      ask_me_later_duration_days="1"/>
 * </php_info_response>
 * </code>
 * <p>
 * XML response format with errors:
 * <code>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <php_info_response>
 *   <sim_version_response error="message"/>
 *   <phet_installer_update_response error="message"/>
 * </php_info_response>
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VersionInfoQuery {
    
    // prints debug output to the System.out
    private static final boolean ENABLE_DEBUG_OUTPUT = true;
    
    private final String project;
    private final String sim;
    private final PhetVersion currentSimVersion;
    private final PhetInstallerVersion currentInstallerVersion;
    private final boolean automaticRequest;
    private final ArrayList listeners;

    public VersionInfoQuery( String project, String sim, PhetVersion currentSimVersion, PhetInstallerVersion currentInstallerVersion, boolean automaticRequest ) {
        this.project = project;
        this.sim = sim;
        this.currentSimVersion = currentSimVersion;
        this.currentInstallerVersion = currentInstallerVersion;
        this.automaticRequest = automaticRequest;
        listeners = new ArrayList();
    }

    public String getProject() {
        return project;
    }

    public String getSim() {
        return sim;
    }

    public PhetVersion getCurrentSimVersion() {
        return currentSimVersion;
    }

    public PhetInstallerVersion getCurrentInstallerVersion() {
        return currentInstallerVersion;
    }

    /**
     * Sends the query to the server.
     * Notifies listeners when the response is received.
     */
    public void send() {
        final String url = PhetCommonConstants.PHET_INFO_URL;
        try {
            // query
            Document queryDocument = buildQueryDocument( project, sim, currentInstallerVersion, automaticRequest );
            if ( ENABLE_DEBUG_OUTPUT ) {
                System.out.println( getClass().getName() + " posting to url=" + url );
                System.out.println( getClass().getName() + " query=\n" + XMLUtils.toString( queryDocument ) );
            }
            HttpURLConnection connection = XMLUtils.post( url, queryDocument );
            
            // response
            Document responseDocument = XMLUtils.readDocument( connection );
            if ( ENABLE_DEBUG_OUTPUT ) {
                System.out.println( getClass().getName() + " response=\n" + XMLUtils.toString( responseDocument ) );
            }
            VersionInfoQueryResponse response = parseResponseDocument( responseDocument, this );
            
            // notification
            if ( response != null ) {
                notifyDone( response );
            }
        }
        catch ( ParserConfigurationException e ) {
            notifyException( e );
        }
        catch ( TransformerException e ) {
            notifyException( e );
        }
        catch ( UnknownHostException e ) {
            notifyException( e );
        }
        catch ( IOException e ) {
            notifyException( e );
        }
        catch ( SAXException e ) {
            notifyException( e );
        }
    }

    /*
     * Creates an XML document that represents the query.
     */
    private static Document buildQueryDocument( String project, String sim, PhetInstallerVersion currentInstallerVersion, boolean automaticRequest ) throws ParserConfigurationException {

        String requestedBy = ( automaticRequest ? "automatic" : "manual" );
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        Element rootElement = document.createElement( "phet_info" );
        document.appendChild( rootElement );

        Element simVersionElement = document.createElement( "sim_version" );
        simVersionElement.setAttribute( "project", project );
        simVersionElement.setAttribute( "sim", sim );
        simVersionElement.setAttribute( "requested_by", requestedBy );
        rootElement.appendChild( simVersionElement );

        Element installerUpdateElement = document.createElement( "phet_installer_update" );
        installerUpdateElement.setAttribute( "timestamp_seconds", String.valueOf( currentInstallerVersion.getTimestamp() ) );
        installerUpdateElement.setAttribute( "requested_by", requestedBy );
        rootElement.appendChild( installerUpdateElement );

        return document;
    }
    
    /*
     * Parses an XML document that represents the query response.
     * If no errors are encountered, returns an object that contains the query results.
     * When the first error is encountered, parsing stops, listeners are notified of an exception, and null is returned.
     */
    private VersionInfoQueryResponse parseResponseDocument( Document document, VersionInfoQuery query ) throws TransformerException {
        
        String elementName, attributeName, attributeValue;
        
        // look for general errors
        elementName = "phet_info_response";
        attributeValue = getAttribute( document, elementName, "error" );
        if ( attributeValue != null ) {
            notifyException( new VersionInfoQueryException( attributeValue ) );
            return null;
        }

        // look for errors in sim_version_response
        elementName = "sim_version_response";
        attributeValue = getAttribute( document, elementName, "error" );
        if ( attributeValue != null ) {
            notifyException( new VersionInfoQueryException( attributeValue ) );
            return null;
        }
        
        // parse sim_version_response
        PhetVersion simVersion;
        long simAskMeLaterDuration;
        {
            elementName = "sim_version_response";

            String versionMajor = getAttribute( document, elementName, "version_major" );
            String versionMinor = getAttribute( document, elementName, "version_minor" );
            String versionDev = getAttribute( document, elementName, "version_dev" );
            String versionRevision = getAttribute( document, elementName, "version_revision" );
            String versionTimestamp = getAttribute( document, elementName, "version_timestamp" );
            if ( versionMajor == null || versionMinor == null || versionDev == null || versionRevision == null || versionTimestamp == null ) {
                notifyException( new VersionInfoQueryException( "missing one or more attribututes related to sim version" ) );
                return null;
            }
            simVersion = new PhetVersion( versionMajor, versionMinor, versionDev, versionRevision, versionTimestamp );

            attributeName = "ask_me_later_duration_days";
            String askMeLaterDuration = getAttribute( document, elementName, attributeName );
            if ( askMeLaterDuration == null ) {
                notifyException( new VersionInfoQueryException( elementName + " is missing attribute " + attributeName ) );
                return null;
            }
            try {
                simAskMeLaterDuration = Long.parseLong( askMeLaterDuration );
            }
            catch ( NumberFormatException e ) {
                notifyException( new VersionInfoQueryException( "expected a number, received " + askMeLaterDuration ) );
                return null;
            }
        }
        
        // look for errors in phet_installer_update_response
        elementName = "phet_installer_update_response";
        attributeValue = getAttribute( document, elementName, "error" );
        if ( attributeValue != null ) {
            notifyException( new VersionInfoQueryException( attributeValue ) );
            return null;
        }

        // parse phet_installer_update_response
        boolean isInstallerUpdateRecommended;
        PhetInstallerVersion installerVersion;
        long installerAskMeLaterDuration;
        {
            elementName = "phet_installer_update_response";
            
            attributeName = "recommend_update";
            attributeValue = getAttribute( document, elementName, attributeName );
            if ( attributeValue == null ) {
                notifyException( new VersionInfoQueryException( elementName + " is missing attribute " + attributeName ) );
            }
            isInstallerUpdateRecommended = Boolean.valueOf( attributeValue ).booleanValue();
            
            attributeName = "timestamp_seconds";
            attributeValue = getAttribute( document, elementName, attributeName );
            if ( attributeValue == null ) {
                notifyException( new VersionInfoQueryException( elementName + " is missing attribute " + attributeName ) );
                return null;
            }
            try {
                installerVersion = new PhetInstallerVersion( Long.parseLong( attributeValue ) );
            }
            catch ( NumberFormatException e ) {
                notifyException( new VersionInfoQueryException( "expected a number, received " + attributeValue ) );
                return null;
            }
            
            attributeName = "ask_me_later_duration_days";
            attributeValue = getAttribute( document, elementName, attributeName );
            if ( attributeValue == null ) {
                notifyException( new VersionInfoQueryException( elementName + " is missing attribute " + attributeName ) );
                return null;
            }
            try {
                installerAskMeLaterDuration = Long.parseLong( attributeValue );
            }
            catch ( NumberFormatException e ) {
                notifyException( new VersionInfoQueryException( "expected a number, received " + attributeValue ) );
                return null;
            }
        }
        
        // response object
        return new VersionInfoQueryResponse( query, simVersion, simAskMeLaterDuration, isInstallerUpdateRecommended, installerVersion, installerAskMeLaterDuration );
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
    
    /**
     * Encapsulates the response to this query.
     */
    public static class VersionInfoQueryResponse {
        
        private final VersionInfoQuery query;
        private final PhetVersion simVersion;
        private final long simAskMeLaterDuration;
        private final boolean isInstallerUpdateRecommended;
        private final PhetInstallerVersion installerVersion;
        private final long installerAskMeLaterDuration;

        public VersionInfoQueryResponse( VersionInfoQuery query, 
                PhetVersion simVersion, long simAskMeLaterDuration, 
                boolean isInstallerUpdateRecommended, PhetInstallerVersion installerVersion, long installerAskMeLaterDuration ) {
            this.query = query;
            this.simVersion = simVersion;
            this.simAskMeLaterDuration = simAskMeLaterDuration;
            this.isInstallerUpdateRecommended = isInstallerUpdateRecommended;
            this.installerVersion = installerVersion;
            this.installerAskMeLaterDuration = installerAskMeLaterDuration;
        }

        public VersionInfoQuery getQuery() {
            return query;
        }

        public boolean isSimUpdateRecommended() {
            return getSimVersion().isGreaterThan( query.getCurrentSimVersion() );
        }

        public PhetVersion getSimVersion() {
            return simVersion;
        }

        public long getSimAskMeLaterDuration() {
            return simAskMeLaterDuration;
        }

        public boolean isInstallerUpdateRecommended() {
            return isInstallerUpdateRecommended;
        }

        public PhetInstallerVersion getInstallerVersion() {
            return installerVersion;
        }

        public long getInstallerAskMeLaterDuration() {
            return installerAskMeLaterDuration;
        }
    }

    public interface VersionInfoQueryListener {

        /**
         * The query is done and results are available.
         * @param result
         */
        public void done( VersionInfoQueryResponse result );

        /**
         * An exception occurred, and don't expect a result.
         * @param e
         */
        public void exception( Exception e );
    }
    
    public static class VersionInfoQueryException extends Exception {
        public VersionInfoQueryException( String message ) {
            super( message );
        }
    }

    public static class VersionInfoQueryAdapter implements VersionInfoQueryListener {

        public void done( VersionInfoQueryResponse result ) {}

        public void exception( Exception e ) {}
    }

    public void addListener( VersionInfoQueryListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( VersionInfoQueryListener listener ) {
        listeners.remove( listener );
    }

    private void notifyDone( VersionInfoQueryResponse result ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (VersionInfoQueryListener) listeners.get( i ) ).done( result );
        }
    }

    private void notifyException( Exception e ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (VersionInfoQueryListener) listeners.get( i ) ).exception( e );
        }
    }
    
    public static void main( String[] args ) {
        PhetVersion simVersion = new PhetVersion( "1", "02", "03", "45678", "1122334455" );
        PhetInstallerVersion installerVersion = new PhetInstallerVersion( 1234567890 );
        VersionInfoQuery query = new VersionInfoQuery( "balloons", "balloons", simVersion, installerVersion, true /* automaticRequest */ );
        query.addListener( new VersionInfoQueryListener() {
            public void done( VersionInfoQueryResponse result ) {
                System.out.println( getClass().getName() + ".done" );
            }

            public void exception( Exception e ) {
                e.printStackTrace();
            }
        });
        query.send();
    }
}
