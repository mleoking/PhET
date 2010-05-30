package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.panels.ComponentThread;
import edu.colorado.phet.website.panels.ComponentThreadStatusPanel;
import edu.colorado.phet.website.panels.LoggerComponentThread;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class WebsiteResourceFinishedPanel extends PhetPanel {

    private Component publisher;
    private AjaxLink revertLink;

    public WebsiteResourceFinishedPanel( String id, final PageContext context, String finalText, final File resourceTmpDir, final File docRoot ) {
        super( id, context );

        add( new RawLabel( "text", finalText ) );

        String path = resourceTmpDir.getAbsolutePath().substring( resourceTmpDir.getAbsolutePath().indexOf( "/staging/resources" ) );
        RawLink dirLink = new RawLink( "link", path );
        add( dirLink );
        dirLink.add( new Label( "name", path ) );

        AjaxLink publishLink = new AjaxLink( "publish-link" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                target.addComponent( this );
                target.addComponent( revertLink );
                setVisible( false );
                revertLink.setVisible( true );

                final File simsDir = PhetWicketApplication.get().getSimulationsRoot();
                final File docRoot = PhetWicketApplication.get().getWebsiteProperties().getPhetDocumentRoot();

                ComponentThread thread = new LoggerComponentThread( WebsiteTranslationDeployPublisher.getLogger() ) {
                    @Override
                    public boolean process() throws IOException, InterruptedException {
                        WebsiteResourceDeployPublisher runner = new WebsiteResourceDeployPublisher( resourceTmpDir, simsDir, docRoot );

                        // open a custom session
                        Session session = HibernateUtils.getInstance().openSession();

                        for ( String projectName : runner.getDeployedProjectNames() ) {
                            Project.synchronizeProject(
                                    docRoot,
                                    session,
                                    projectName,
                                    WebsiteTranslationDeployPublisher.getLogger()
                            );
                        }

                        session.close();

                        return true;
                    }

                };

                Component newPublisher = new ComponentThreadStatusPanel( "publisher", context, thread, 1000 );
                newPublisher.setOutputMarkupId( true );
                newPublisher.setMarkupId( "deploy-publisher" );
                publisher.replaceWith( newPublisher );
                target.addComponent( newPublisher );

                thread.start();
            }
        };
        publishLink.setOutputMarkupId( true );
        add( publishLink );

        publisher = new Label( "publisher", "" );
        publisher.setOutputMarkupId( true );
        publisher.setMarkupId( "deploy-publisher" );
        add( publisher );

        revertLink = new AjaxLink( "reverter" ) {
            @Override
            public void onClick( AjaxRequestTarget target ) {
                target.addComponent( this );
                setVisible( false );
                final File simsDir = PhetWicketApplication.get().getSimulationsRoot();
                new WebsiteResourceDeployReverter( resourceTmpDir, simsDir );
            }
        };
        revertLink.setVisible( false );
        revertLink.setOutputMarkupId( true );
        add( revertLink );
    }
}