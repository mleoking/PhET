package edu.colorado.phet.website.services;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.colorado.phet.common.phetcommon.view.util.XMLUtils;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.HtmlUtils;
import edu.colorado.phet.website.util.PhetRequestCycle;

/**
 * Replacement phet-info service
 */
public class PhetInfoServicePage extends WebPage {

    private static Logger logger = Logger.getLogger( PhetInfoServicePage.class.getName() );
    private StringBuilder response;

    public static final int INSTALL_RECOMMEND_UPDATE_DAYS = 60;
    public static final int SIM_ASK_ME_LATER_DAYS = 1;
    public static final int INSTALLER_ASK_ME_LATER_DAYS = 30;

    // TODO: fix this!!! (when we have a system to identify which is the latest installer timestamp)
    public static final int TIMESTAMP = 1255450853;

    public PhetInfoServicePage() {
        HttpServletRequest request = ( (PhetRequestCycle) getRequestCycle() ).getWebRequest().getHttpServletRequest();
        String rawData = (String) request.getAttribute( "raw-data" );

        logger.warn( "rawData: " + rawData );

        response = new StringBuilder();

        boolean overallSuccess = true;

        try {
            Document document = XMLUtils.toDocument( rawData );

            NodeList simVersions = document.getElementsByTagName( "sim_version" );

            for ( int i = 0; i < simVersions.getLength(); i++ ) {
                Element element = (Element) simVersions.item( 0 );
                final String project = element.getAttribute( "project" );
                final String sim = element.hasAttribute( "sim" ) ? element.getAttribute( "sim" ) : project;
                final String version = element.getAttribute( "request_version" );
                if ( version == null || !version.equals( "1" ) ) {
                    response.append( "<sim_version_response success=\"false\" project=\"" + quoteEscape( project ) + "\" sim=\"" + quoteEscape( sim ) + "\"><error>" );
                    response.append( "Unsupported sim_version request_version: " + quoteEscape( version ) );
                    response.append( "</error></sim_version_response>" );
                    continue;
                }
                final Simulation[] sims = new Simulation[1];
                boolean success = HibernateUtils.wrapSession( new HibernateTask() {
                    public boolean run( Session session ) {
                        Simulation simulation = (Simulation) session.createQuery( "select s from Simulation as s where s.name = :sim and s.project.name = :project" )
                                .setString( "project", project ).setString( "sim", sim ).uniqueResult();
                        sims[0] = simulation;
                        return true;
                    }
                } );
                Project proj = sims[0].getProject();
                if ( success ) {
                    response.append( "<sim_version_response success=\"true\"" );
                    response.append( " project=\"" + quoteEscape( project ) + "\"" );
                    response.append( " sim=\"" + quoteEscape( sim ) + "\"" );
                    response.append( " version_major=\"" + proj.getVersionMajor() + "\"" );
                    response.append( " version_minor=\"" + doublePadNumber( proj.getVersionMinor() ) + "\"" );
                    response.append( " version_dev=\"" + doublePadNumber( proj.getVersionDev() ) + "\"" );
                    response.append( " version_revision=\"" + proj.getVersionRevision() + "\"" );
                    response.append( " version_timestamp=\"" + proj.getVersionTimestamp() + "\"" );
                    response.append( " ask_me_later_duration_days=\"" + SIM_ASK_ME_LATER_DAYS + "\"" );
                    response.append( "/>" );
                }
                else {
                    response.append( "<sim_version_response success=\"false\" project=\"" + quoteEscape( project ) + "\" sim=\"" + quoteEscape( sim ) + "\"><error>" );
                    response.append( "Unable to find a simulation matching project:sim of " + quoteEscape( project ) + ":" + quoteEscape( sim ) );
                    response.append( "</error></sim_version_response>" );
                }
            }

            NodeList installerUpdates = document.getElementsByTagName( "phet_installer_update" );

            for ( int i = 0; i < installerUpdates.getLength(); i++ ) {
                Element element = (Element) installerUpdates.item( i );
                final String version = element.getAttribute( "request_version" );
                if ( version == null || !version.equals( "1" ) ) {
                    response.append( "<phet_installer_update_response success=\"false\"><error>" );
                    response.append( "Unsupported phet_installer_update request_version: " + quoteEscape( version ) );
                    response.append( "</error></phet_installer_update_response>" );
                    continue;
                }
                String timestampInSeconds = element.getAttribute( "timestamp_seconds" );
                int timestamp = Integer.valueOf( timestampInSeconds );

                boolean recommendUpdate = timestamp + INSTALL_RECOMMEND_UPDATE_DAYS * 60 * 60 * 24 < TIMESTAMP;

                response.append( "<phet_installer_update_response success=\"true\"" );
                response.append( " recommend_update=\"" + recommendUpdate + "\"" );
                response.append( " timestamp_seconds=\"" + TIMESTAMP + "\"" );
                response.append( " ask_me_later_duration_days=\"" + INSTALLER_ASK_ME_LATER_DAYS + "\"" );
                response.append( "/>" );

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

        if ( overallSuccess == false ) {
            Label label = new Label( "response", "<error>Unable to </error>" );
            add( label );
            label.add( new AttributeModifier( "success", true, new Model( "false" ) ) );
        }
        else {
            Label label = new Label( "response", response.toString() );
            add( label );
            label.setEscapeModelStrings( false );
            label.add( new AttributeModifier( "success", true, new Model( "true" ) ) );
        }

        // don't save anything
        response = null;


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
