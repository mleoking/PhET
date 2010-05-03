package edu.colorado.phet.website.admin;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.pages.RedirectPage;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Called during the deployment process from within the server, meant to synchronize a particular project
 */
public class AdminSynchronizeProjectPage extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( AdminSynchronizeProjectPage.class.getName() );


    public AdminSynchronizeProjectPage( PageParameters parameters ) {
        super( parameters );

        String addr = getPhetCycle().getWebRequest().getHttpServletRequest().getRemoteAddr();
        String host = getPhetCycle().getWebRequest().getHttpServletRequest().getRemoteHost();
        String localhost = getPhetCycle().getWebRequest().getHttpServletRequest().getServerName();

        logger.debug( "addr: " + addr );
        logger.debug( "host: " + host );
        logger.debug( "localhost: " + localhost );

        if ( localhost.equals( host ) ) {
            String projectName = parameters.getString( "project" );

            logger.info( "synchronizing project: " + projectName );

            Project.synchronizeProject( PhetWicketApplication.get().getPhetDocumentRoot(), getHibernateSession(), projectName );
        }

        // redirect back to contribution page
        setResponsePage( new RedirectPage( "/" ) );

    }

    public static RawLinkable getLinker( final String projectName ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "admin/synchronize?project=" + projectName;
            }
        };
    }

    public static RawLinkable getLinker( Project project ) {
        return getLinker( project.getName() );
    }

}