package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.admin.AdminPage;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.panels.ComponentThread;
import edu.colorado.phet.website.panels.ComponentThreadStatusPanel;
import edu.colorado.phet.website.util.HtmlUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Called during the deployment process from within the server, meant to synchronize a particular project
 */
public class DeployTranslationPage extends AdminPage {

    private static Logger logger = Logger.getLogger( DeployTranslationPage.class.getName() );

    private static final Object lock = new Object();


    public DeployTranslationPage( PageParameters parameters ) {
        super( parameters );

        synchronized( lock ) {
            add( new Label( "dir", parameters.getString( "dir" ) ) );

            final File translationDir = new File( parameters.getString( "dir" ) );

            try {
                if ( !translationDir.getCanonicalPath().startsWith( PhetWicketApplication.get().getStagingRoot().getCanonicalPath() ) ) {
                    logger.warn( "unacceptable translation dir path: " + translationDir.getCanonicalPath() );
                    logger.warn( "not under " + PhetWicketApplication.get().getStagingRoot().getCanonicalPath() );
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }

            final String jarPath = PhetWicketApplication.get().getWebsiteProperties().getPathToJarUtility();
            final File simsDir = PhetWicketApplication.get().getSimulationsRoot();

            ComponentThread thread = new ComponentThread() {

                private StringWriter writer = new StringWriter();

                @Override
                public Component getComponent( String id, PageContext context ) {
                    String output = writer.toString();
                    output = HtmlUtils.encode( output ).replace( "\n", "<br/>" );

                    if ( isDone() ) {
                        // TODO: check for errors!
                        output = "<h2>Complete, check for errors?</h2><br/>" + output;
                    }
                    
                    return new RawLabel( id, output );
                }

                @Override
                public void run() {
                    // add an appender so we can capture the info
                    WriterAppender appender = new WriterAppender( new PatternLayout(), writer );
                    WebsiteTranslationDeployServer.getLogger().addAppender( appender );
                    try {
                        WebsiteTranslationDeployServer runner = new WebsiteTranslationDeployServer(
                                jarPath,
                                BuildLocalProperties.getInstance(),
                                simsDir
                        );

                        runner.integrateTranslations( translationDir );
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                        WebsiteTranslationDeployServer.getLogger().error( "IOException", e );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                        WebsiteTranslationDeployServer.getLogger().error( "InterruptedException", e );
                    }
                    catch( RuntimeException e ) {
                        e.printStackTrace();
                        WebsiteTranslationDeployServer.getLogger().error( "RuntimeException", e );
                    }
                    finish();

                    WebsiteTranslationDeployServer.getLogger().removeAppender( appender );
                }
            };

            add( new ComponentThreadStatusPanel( "test", getPageContext(), thread, 1000 ) );

            thread.start();
        }
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