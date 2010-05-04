package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.WebsiteProperties;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Called during the deployment process from within the server, meant to synchronize a particular project
 */
public class AdminDeployProjectPage extends PhetRegularPage {

    private static Logger logger = Logger.getLogger( AdminDeployProjectPage.class.getName() );


    public AdminDeployProjectPage( PageParameters parameters ) {
        super( parameters );

        String addr = getPhetCycle().getWebRequest().getHttpServletRequest().getRemoteAddr();
        String host = getPhetCycle().getWebRequest().getHttpServletRequest().getRemoteHost();
        String localhost = getPhetCycle().getWebRequest().getHttpServletRequest().getServerName();

        logger.debug( "addr: " + addr );
        logger.debug( "host: " + host );
        logger.debug( "localhost: " + localhost );

        if ( localhost.equals( host ) ) {
            deploy( parameters );
        }

        // TODO: return information to PBG?
    }

    private void deploy( PageParameters parameters ) {
        WebsiteProperties websiteProperties = PhetWicketApplication.get().getWebsiteProperties();
        File docRoot = websiteProperties.getPhetDocumentRoot();

        boolean success = true;

        String projectName = parameters.getString( "project" );

        logger.info( "backing up project " + projectName );

        success = Project.backupProject( docRoot, projectName );
        if ( !success ) {
            logger.error( "error backing up project " + projectName + ", aborting deployment" );
        }

        File stagingDir = new File( websiteProperties.getSimStagingArea(), projectName );
        if ( !stagingDir.exists() ) {
            logger.error( "staging directory does not exist, aborting deployment" );
        }

        File projectDir = new File( websiteProperties.getPhetDocumentRoot(), "sims/" + projectName );
        for ( File file : projectDir.listFiles() ) {
            file.delete();
        }

        try {
            FileUtils.copyRecursive( stagingDir, projectDir );
        }
        catch( IOException e ) {
            e.printStackTrace();
            logger.error( "Attempting to recover after failed copy to project dir", e );
            // TODO: attempt to recover
            return;
        }

        logger.info( "synchronizing project: " + projectName );

        Project.synchronizeProject( docRoot, getHibernateSession(), projectName );

        logger.info( "removing staging directory" );

        FileUtils.delete( stagingDir );
    }

    public static RawLinkable getLinker( final String projectName ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "admin/deploy?project=" + projectName;
            }
        };
    }

    public static RawLinkable getLinker( Project project ) {
        return getLinker( project.getName() );
    }

}