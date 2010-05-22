package edu.colorado.phet.website.authentication;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.templates.PhetPage;

public class AuthenticatedPage extends PhetPage {
    public AuthenticatedPage( PageParameters parameters ) {
        super( parameters, true );

        if ( !PhetSession.get().isSignedIn() ) {
            throw new RestartResponseAtInterceptPageException( SignInPage.class );
        }

    }

    public PhetUser getUser() {
        return PhetSession.get().getUser();
    }

    public static void checkSignedIn() {
        if ( !PhetSession.get().isSignedIn() ) {
            throw new RestartResponseAtInterceptPageException( SignInPage.class );
        }
    }

}
