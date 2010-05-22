package edu.colorado.phet.website.admin.deploy;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.website.admin.AdminPage;
import edu.colorado.phet.website.panels.ComponentThread;
import edu.colorado.phet.website.panels.ComponentThreadStatusPanel;
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

            ComponentThread thread = new ComponentThread() {
                @Override
                public Component getComponent( String id, PageContext context ) {
                    return new Label( id, String.valueOf( ( new Date() ).getTime() ) );
                }

                @Override
                public void run() {
                    for( int i = 0; i < 20; i++ ) {
                        try {
                            Thread.sleep( 1000 );
                        }
                        catch( InterruptedException e ) {
                            e.printStackTrace();
                        }
                    }
                    finish();
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