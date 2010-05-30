package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.admin.AdminPage;
import edu.colorado.phet.website.panels.ComponentThread;
import edu.colorado.phet.website.panels.ComponentThreadStatusPanel;
import edu.colorado.phet.website.panels.LoggerComponentThread;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Called during the deployment process from within the server, meant to synchronize a particular project
 */
public class DeployTranslationPage extends AdminPage {

    private static final Logger logger = Logger.getLogger( DeployTranslationPage.class.getName() );
    private static final Set<File> usedDirs = new HashSet<File>();
    private static final Object usedLock = new Object();

    public DeployTranslationPage( PageParameters parameters ) {
        super( parameters );

        add( new Label( "dir", parameters.getString( "dir" ) ) );

        final File translationDir = new File( parameters.getString( "dir" ) );

        try {
            if ( !translationDir.getCanonicalPath().startsWith( PhetWicketApplication.get().getStagingRoot().getCanonicalPath() ) ) {
                logger.error( "unacceptable translation dir path: " + translationDir.getCanonicalPath() );
                logger.error( "not under " + PhetWicketApplication.get().getStagingRoot().getCanonicalPath() );
                add( new Label( "response", "PATH error, see logger" ) );
                return;
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        if ( !translationDir.exists() ) {
            add( new Label( "response", "PATH error, directory does not exist" ) );
            return;
        }

        if ( new File( translationDir, "finished.txt" ).exists() ) {
            add( new WebsiteTranslationFinishedPanel( "response", getPageContext(), "Ready for testing", translationDir, PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot() ) );
        }
        else if ( new File( translationDir, "error.txt" ).exists() ) {
            add( new Label( "response", "There was an error encountered in the processing" ) );
        }
        else {
            boolean ok;
            synchronized( usedLock ) {
                ok = !usedDirs.contains( translationDir );
                if ( ok ) {
                    usedDirs.add( translationDir );
                }
            }
            if ( ok ) {
                startServer( translationDir );
            }
            else {
                add( new Label( "response", "This directory is being processed or another error has occurred. " +
                                            "Please refer to the original window." ) );
            }
        }
    }

    private void startServer( final File translationDir ) {
        final String jarPath = PhetWicketApplication.get().getWebsiteProperties().getPathToJarUtility();
        final File simsDir = PhetWicketApplication.get().getSimulationsRoot();

        ComponentThread thread = new LoggerComponentThread( WebsiteTranslationDeployServer.getLogger() ) {
            @Override
            public boolean process() throws IOException, InterruptedException {
                WebsiteTranslationDeployServer runner = new WebsiteTranslationDeployServer(
                        jarPath,
                        BuildLocalProperties.getInstance(),
                        simsDir
                );

                runner.integrateTranslations( translationDir );

                FileUtils.writeString( new File( translationDir, "finished.txt" ), "finished at " + new Date() );
                return true;
            }

            @Override
            public Component getFinishedComponent( String id, String rawText ) {
                return new WebsiteTranslationFinishedPanel( id, getPageContext(), rawText, translationDir, PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot() );
            }

            @Override
            public void onError() {
                try {
                    FileUtils.writeString( new File( translationDir, "error.txt" ), "errored at " + new Date() );
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
                return "admin/deploy-translation?dir=" + dir;
            }
        };
    }

}