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

        final String jarPath = PhetWicketApplication.get().getWebsiteProperties().getPathToJarUtility();
        final File simsDir = PhetWicketApplication.get().getSimulationsRoot();

        ComponentThread thread = new ComponentThread() {

            private StringWriter writer = new StringWriter();
            private boolean error = false;

            @Override
            public Component getComponent( String id, PageContext context ) {
                String output = writer.toString();
                output = HtmlUtils.encode( output ).replace( "\n", "<br/>" );

                String header;

                if ( isDone() ) {
                    if ( error ) {
                        header = "<strong style=\"color: #FF0000;\">Errors encountered</strong><br/>";
                    }
                    else {
                        header = "<strong style=\"color: #008800;\">Completed without errors</strong><br/>";
                    }
                }
                else {
                    header = "<strong style=\"color: #0000FF;\">Processing, please wait.</strong><br/>updated";
                }

                header += " at " + ( new Date() ).toString() + "<br/>";

                String text = header + output;

                if ( isDone() ) {
                    return new TranslationDeployServerFinishedPanel( id, context, text, translationDir.listFiles() );
                }
                else {
                    return new RawLabel( id, text );
                }
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
                    error = true;
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                    WebsiteTranslationDeployServer.getLogger().error( "InterruptedException", e );
                    error = true;
                }
                catch( RuntimeException e ) {
                    e.printStackTrace();
                    WebsiteTranslationDeployServer.getLogger().error( "RuntimeException", e );
                    error = true;
                }
                finish();
                WebsiteTranslationDeployServer.getLogger().info( "Finishing" );

                WebsiteTranslationDeployServer.getLogger().removeAppender( appender );
            }
        };

        add( new ComponentThreadStatusPanel( "response", getPageContext(), thread, 1000 ) );

        try {
            thread.start();
        }
        catch( RuntimeException e ) {
            e.printStackTrace();
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