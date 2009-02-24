
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
import org.xml.sax.SAXException;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.resources.PhetInstallerVersion;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.view.util.XMLUtils;


public class VersionInfoQuery {
    
    // prints debug output to the System.out
    private static final boolean ENABLE_DEBUG_OUTPUT = true;
    
    private final String project;
    private final String sim;
    private final PhetVersion currentSimVersion;
    private final PhetInstallerVersion currentInstallerVersion;
    private final ArrayList listeners;

    public VersionInfoQuery( String project, String sim, PhetVersion currentSimVersion, PhetInstallerVersion currentInstallerVersion ) {
        this.project = project;
        this.sim = sim;
        this.currentSimVersion = currentSimVersion;
        this.currentInstallerVersion = currentInstallerVersion;
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
            Document queryDocument = buildQueryDocument( project, sim, currentInstallerVersion );
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
            notifyDone( response );
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

    private static Document buildQueryDocument( String project, String sim, PhetInstallerVersion currentInstallerVersion ) throws ParserConfigurationException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        Element rootElement = document.createElement( "phet_info" );
        document.appendChild( rootElement );

        Element simVersionElement = document.createElement( "sim_version" );
        simVersionElement.setAttribute( "project", project );
        simVersionElement.setAttribute( "sim", sim );
        simVersionElement.setAttribute( "requested_by", "auto" );//TODO
        rootElement.appendChild( simVersionElement );

        Element installerUpdateElement = document.createElement( "phet_installer_update" );
        installerUpdateElement.setAttribute( "timestamp_seconds", String.valueOf( currentInstallerVersion.getTimestamp() ) );
        installerUpdateElement.setAttribute( "requested_by", "auto" );//TODO
        rootElement.appendChild( installerUpdateElement );

        return document;
    }
    
    private static VersionInfoQueryResponse parseResponseDocument( Document document, VersionInfoQuery query ) {
        //TODO read document
        PhetVersion simVersion = new PhetVersion( "2", "01", "00", "999999", "123456789" );//TODO
        long simAskMeLaterDuration = 1; //TODO
        boolean isInstallerUpdateRecommended = true;//TODO
        PhetInstallerVersion installerVersion = new PhetInstallerVersion( 1234567890 );//TODO
        long installerAskMeLaterDuration = 30;//TODO
        return new VersionInfoQueryResponse( query, simVersion, simAskMeLaterDuration, isInstallerUpdateRecommended, installerVersion, installerAskMeLaterDuration );
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
        VersionInfoQuery query = new VersionInfoQuery( "balloons", "balloons", simVersion, installerVersion );
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
