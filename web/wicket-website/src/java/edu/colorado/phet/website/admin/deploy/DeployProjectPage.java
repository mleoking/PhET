package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.JARGenerator;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.WebsiteProperties;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Called during the deployment process from within the server, meant to synchronize a particular project
 */
public class DeployProjectPage extends PhetPage {

    private static Logger logger = Logger.getLogger( DeployProjectPage.class.getName() );

    private static final Object lock = new Object();


    public DeployProjectPage( PageParameters parameters ) {
        super( parameters );

        synchronized( lock ) {


            String addr = getPhetCycle().getWebRequest().getHttpServletRequest().getRemoteAddr();
            String host = getPhetCycle().getWebRequest().getHttpServletRequest().getRemoteHost();
            String localhost = getPhetCycle().getWebRequest().getHttpServletRequest().getServerName();

            logger.debug( "addr: " + addr );
            logger.debug( "host: " + host );
            logger.debug( "localhost: " + localhost );

            if ( getPhetCycle().isLocalRequest() ) {
                deploy( parameters );
            }

            addTitle( " deployment" );

            // TODO: return information to PBG?

        }

    }

    private void deploy( PageParameters parameters ) {
        WebsiteProperties websiteProperties = PhetWicketApplication.get().getWebsiteProperties();
        File docRoot = websiteProperties.getPhetDocumentRoot();

        boolean success;

        String projectName = parameters.getString( "project" );
        boolean generateJARs = parameters.getString( "generate-jars" ).equals( "true" );

        logger.info( "generateJARs set as: " + generateJARs );

        logger.info( "backing up project " + projectName );

        success = Project.backupProject( docRoot, projectName );
        if ( !success ) {
            logger.error( "error backing up project " + projectName + ", aborting deployment" );
            return;
        }

        File stagingDir = new File( websiteProperties.getSimStagingArea(), projectName );
        if ( !stagingDir.exists() ) {
            logger.error( "staging directory does not exist, aborting deployment" );
            return;
        }

        if ( generateJARs ) {
            File allJAR = new File( stagingDir, projectName + "_all.jar" );
            if ( !allJAR.exists() ) {
                logger.error( "_all.jar does not exist, aborting deployment" );
            }

            try {
                ( new JARGenerator() ).generateOfflineJARs( allJAR, websiteProperties.getPathToJarUtility(), BuildLocalProperties.getInstance() );
            }
            catch( IOException e ) {
                e.printStackTrace();
                return;
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
                return;
            }
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

        // will clear the necessary caches, along with updating the database. woo!
        Project.synchronizeProject( docRoot, getHibernateSession(), projectName );

        logger.info( "removing staging directory" );

        FileUtils.delete( stagingDir );
    }

    public static RawLinkable getLinker( final String projectName, final boolean generateJars ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "admin/deploy?project=" + projectName + "&generate-jars=" + generateJars;
            }
        };
    }

    public static RawLinkable getLinker( Project project, final boolean generateJars ) {
        return getLinker( project.getName(), generateJars );
    }

}