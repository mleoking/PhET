package edu.colorado.phet.wickettest.authentication;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.wickettest.content.IndexPage;
import edu.colorado.phet.wickettest.templates.PhetPage;
import edu.colorado.phet.wickettest.util.PhetSession;

public class AuthenticatedPage extends PhetPage {
    public AuthenticatedPage( PageParameters parameters ) {
        super( parameters, true );

        if ( !PhetSession.get().isSignedIn() ) {
            throw new RestartResponseAtInterceptPageException( SignInPage.class );
        }

        add( new Link( "sign-out" ) {
            public void onClick() {
                signOut();
                setResponsePage( IndexPage.class );
            }
        } );

    }

    public void signOut() {
        getSession().invalidate();
    }
}
