package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.hibernate.Session;

import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.admin.AdminPage;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.panels.ComponentThread;
import edu.colorado.phet.website.panels.ComponentThreadStatusPanel;
import edu.colorado.phet.website.panels.LoggerComponentThread;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class DeployResourcePage extends AdminPage {

    private static final Logger logger = Logger.getLogger( DeployResourcePage.class.getName() );
    private static final Set<File> usedDirs = new HashSet<File>();
    private static final Object usedLock = new Object();

    public DeployResourcePage( PageParameters parameters ) {
        super( parameters );

        add( new Label( "dir", parameters.getString( "dir" ) ) );

        final File resourceTmpDir = new File( parameters.getString( "dir" ) );

        try {
            if ( !resourceTmpDir.getCanonicalPath().startsWith( PhetWicketApplication.get().getStagingRoot().getCanonicalPath() ) ) {
                logger.error( "unacceptable resource dir path: " + resourceTmpDir.getCanonicalPath() );
                logger.error( "not under " + PhetWicketApplication.get().getStagingRoot().getCanonicalPath() );
                add( new Label( "response", "PATH error, see logger" ) );
                return;
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        if ( !resourceTmpDir.exists() ) {
            add( new Label( "response", "PATH error, directory does not exist" ) );
            return;
        }

        if ( new File( resourceTmpDir, "finished.txt" ).exists() ) {
            add( new TranslationDeployServerFinishedPanel( "response", getPageContext(), "Ready for testing", resourceTmpDir, PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot() ) );
        }
        else if ( new File( resourceTmpDir, "error.txt" ).exists() ) {
            add( new Label( "response", "There was an error encountered in the processing" ) );
        }
        else {
            boolean ok;
            synchronized( usedLock ) {
                ok = !usedDirs.contains( resourceTmpDir );
                if ( ok ) {
                    usedDirs.add( resourceTmpDir );
                }
            }
            if ( ok ) {
                startServer( resourceTmpDir );
            }
            else {
                add( new Label( "response", "This directory is being processed or another error has occurred. " +
                                            "Please refer to the original window." ) );
            }
        }
    }

    private void startServer( final File resourceTmpDir ) {
        // TODO: setup backing up of projects
        final String jarPath = PhetWicketApplication.get().getWebsiteProperties().getPathToJarUtility();
        final File docRoot = PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot();

        final List<Logger> loggers = new LinkedList<Logger>();

        loggers.add( Logger.getLogger( WebsiteResourceDeployServer.class ) );
        loggers.add( Logger.getLogger( Project.class ) );
        loggers.add( Logger.getLogger( "edu.colorado.phet.buildtools" ) );
        //loggers.add( Logger.getLogger( "edu.colorado.phet.phetcommon" ) );

        // TODO: turn thread into a thread that sends events. listeners can then handle multiple "listener" panels for one thread
        ComponentThread thread = new LoggerComponentThread( loggers ) {
            @Override
            public boolean process() throws IOException, InterruptedException {
                WebsiteResourceDeployServer runner = new WebsiteResourceDeployServer(
                        jarPath,
                        resourceTmpDir
                );

                Session session = HibernateUtils.getInstance().openSession();
                for ( String projectName : runner.getExistingProjects() ) {
                    Project.backupProject( docRoot, projectName );
                }
                session.close();

                FileUtils.writeString( new File( resourceTmpDir, "finished.txt" ), "finished at " + new Date() );
                return true;
            }

            @Override
            public Component getFinishedComponent( String id, String rawText ) {
                //return new TranslationDeployServerFinishedPanel( id, getPageContext(), rawText, resourceTmpDir, PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot() );
                return new RawLabel( id, rawText );
            }

            @Override
            public void onError() {
                try {
                    FileUtils.writeString( new File( resourceTmpDir, "error.txt" ), "errored at " + new Date() );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }

        };

        add( new ComponentThreadStatusPanel( "response", getPageContext(), thread, 1000 ) );

        thread.start();
    }

    public static RawLinkable getLinker( final String dir ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "admin/deploy-resource?dir=" + dir;
            }
        };
    }

}