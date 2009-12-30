package edu.colorado.phet.website.panels;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.link.StatelessLink;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.authentication.SignInPage;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.content.IndexPage;
import edu.colorado.phet.website.util.PageContext;

public class LogInOutPanel extends PhetPanel {
                                                                                                                                               
    // TODO: i18nize

    public LogInOutPanel( String id, PageContext context ) {
        super( id, context );

        final PhetSession psession = PhetSession.get();
        if ( psession != null && psession.isSignedIn() ) {
            add( new StatelessLink( "sign-out" ) {
                public void onClick() {
                    PhetSession.get().signOut();
                    setResponsePage( IndexPage.class );
                }
            } );
            add( new InvisibleComponent( "sign-in" ) );
        }
        else {
            add( new InvisibleComponent( "sign-out" ) );
            if ( DistributionHandler.displayLogin( getPhetCycle() ) ) {
                add( new StatelessLink( "sign-in" ) {
                    public void onClick() {
                        throw new RestartResponseAtInterceptPageException( SignInPage.class );
                    }
                } );
            }
            else {
                add( new InvisibleComponent( "sign-in" ) );
            }
        }
    }
}
