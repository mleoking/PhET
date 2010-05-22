package edu.colorado.phet.website.services;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.colorado.phet.common.phetcommon.view.util.XMLUtils;
import edu.colorado.phet.website.components.RawBodyLabel;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.HtmlUtils;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.components.RawLabel;

/**
 * Replacement phet-info service
 */
public class PhetInfoServicePage extends WebPage {

    // TODO: allow changing during runtime
    public static final int INSTALL_RECOMMEND_UPDATE_DAYS = 60;
    public static final int SIM_ASK_ME_LATER_DAYS = 1;
    public static final int INSTALLER_ASK_ME_LATER_DAYS = 30;

    public static final String SIM_VERSION_RESPONSE_TAG = "sim_version_response";
    public static final String INSTALLER_UPDATE_RESPONSE_TAG = "phet_installer_update_response";

    // TODO: fix when we have a system to identify which is the latest installer timestamp
    public static final int TIMESTAMP = 1255450853;

    private static final Logger logger = Logger.getLogger( PhetInfoServicePage.class.getName() );


    public PhetInfoServicePage() {

        // snag the raw POST data from the PreFilter
        HttpServletRequest request = ( (PhetRequestCycle) getRequestCycle() ).getWebRequest().getHttpServletRequest();
        String rawData = (String) request.getAttribute( "raw-data" );

        boolean overallSuccess = true;

        Document outDocument = null;
        try {
            outDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }
        catch( ParserConfigurationException e ) {
            logger.warn( "unable to create a document" );
            e.printStackTrace();
            return;
        }

        Element root = outDocument.createElement( "phet_info_response" );
        outDocument.appendChild( root );

        try {
            Document inDocument = XMLUtils.toDocument( rawData );

            // handle all sim version queries
            NodeList simVersions = inDocument.getElementsByTagName( "sim_version" );
            for ( int i = 0; i < simVersions.getLength(); i++ ) {
                Element element = (Element) simVersions.item( 0 );
                writeSimVersionResponse( outDocument, root, element );
            }

            // handle all installer update queries
            NodeList installerUpdates = inDocument.getElementsByTagName( "phet_installer_update" );
            for ( int i = 0; i < installerUpdates.getLength(); i++ ) {
                Element element = (Element) installerUpdates.item( i );
                writeInstallerUpdateResponse( outDocument, root, element );
            }
        }
        catch( TransformerException e ) {
            e.printStackTrace();
            overallSuccess = false;
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
            overallSuccess = false;
        }

        // set success based on parsing
        root.setAttribute( "success", String.valueOf( overallSuccess ) );

        try {
            add( new RawBodyLabel( "response", XMLUtils.toString( outDocument ) ) );
        }
        catch( TransformerException e ) {
            logger.error( "error converting XML into a string" );
            e.printStackTrace();
        }

    }

    private void writeSimVersionResponse( Document document, Element root, Element queryElement ) {
        final String project = queryElement.getAttribute( "project" );
        final String sim = queryElement.hasAttribute( "sim" ) ? queryElement.getAttribute( "sim" ) : project;
        final String version = queryElement.getAttribute( "request_version" );
        if ( version == null || !version.equals( "1" ) ) {
            Element simVersionResponse = document.createElement( SIM_VERSION_RESPONSE_TAG );
            simVersionResponse.setAttribute( "success", "false" );
            simVersionResponse.setAttribute( "project", project );
            simVersionResponse.setAttribute( "sim", sim );
            Element errorElement = document.createElement( "error" );
            simVersionResponse.appendChild( errorElement );
            errorElement.setTextContent( "Unsupported sim_version request_version: " + quoteEscape( version ) );

            root.appendChild( simVersionResponse );
            return;
        }
        final Simulation[] sims = new Simulation[1];
        boolean foundSim = HibernateUtils.wrapSession( new HibernateTask() {
            public boolean run( Session session ) {
                Simulation simulation = (Simulation) session.createQuery( "select s from Simulation as s where s.name = :sim and s.project.name = :project" )
                        .setString( "project", project ).setString( "sim", sim ).uniqueResult();
                sims[0] = simulation;
                return true;
            }
        } );
        Project proj = sims[0].getProject();
        if ( foundSim ) {
            Element simVersionResponse = document.createElement( SIM_VERSION_RESPONSE_TAG );
            simVersionResponse.setAttribute( "success", "true" );
            simVersionResponse.setAttribute( "project", project );
            simVersionResponse.setAttribute( "sim", sim );

            simVersionResponse.setAttribute( "version_major", String.valueOf( proj.getVersionMajor() ) );
            simVersionResponse.setAttribute( "version_minor", doublePadNumber( proj.getVersionMinor() ) );
            simVersionResponse.setAttribute( "version_dev", doublePadNumber( proj.getVersionDev() ) );
            simVersionResponse.setAttribute( "version_revision", String.valueOf( proj.getVersionRevision() ) );
            simVersionResponse.setAttribute( "version_timestamp", String.valueOf( proj.getVersionTimestamp() ) );
            simVersionResponse.setAttribute( "ask_me_later_duration_days", String.valueOf( SIM_ASK_ME_LATER_DAYS ) );

            root.appendChild( simVersionResponse );
        }
        else {
            Element simVersionResponse = document.createElement( SIM_VERSION_RESPONSE_TAG );
            simVersionResponse.setAttribute( "success", "false" );
            simVersionResponse.setAttribute( "project", project );
            simVersionResponse.setAttribute( "sim", sim );
            Element errorElement = document.createElement( "error" );
            simVersionResponse.appendChild( errorElement );
            errorElement.setTextContent( "Unable to find a simulation matching project:sim of " + quoteEscape( project ) + ":" + quoteEscape( sim ) );

            root.appendChild( simVersionResponse );
        }
    }

    private void writeInstallerUpdateResponse( Document document, Element root, Element queryElement ) {
        final String version = queryElement.getAttribute( "request_version" );
        if ( version == null || !version.equals( "1" ) ) {
            Element installerResponse = document.createElement( INSTALLER_UPDATE_RESPONSE_TAG );
            installerResponse.setAttribute( "success", "false" );
            Element errorElement = document.createElement( "error" );
            installerResponse.appendChild( errorElement );
            errorElement.setTextContent( "Unsupported phet_installer_update request_version: " + quoteEscape( version ) );

            root.appendChild( installerResponse );
            return;
        }

        int clientInstallerTimestamp = Integer.valueOf( queryElement.getAttribute( "timestamp_seconds" ) );

        boolean recommendUpdate = clientInstallerTimestamp + INSTALL_RECOMMEND_UPDATE_DAYS * 60 * 60 * 24 < TIMESTAMP;

        Element installerResponse = document.createElement( INSTALLER_UPDATE_RESPONSE_TAG );
        installerResponse.setAttribute( "success", "true" );
        installerResponse.setAttribute( "recommend_update", String.valueOf( recommendUpdate ) );
        installerResponse.setAttribute( "timestamp_seconds", String.valueOf( TIMESTAMP ) );
        installerResponse.setAttribute( "ask_me_later_duration_days", String.valueOf( INSTALLER_ASK_ME_LATER_DAYS ) );

        root.appendChild( installerResponse );
    }

    private String doublePadNumber( int num ) {
        if ( num < 10 ) {
            return "0" + num;
        }
        else {
            return "" + num;
        }
    }

    private String quoteEscape( String str ) {
        return HtmlUtils.encode( str );
    }
}
