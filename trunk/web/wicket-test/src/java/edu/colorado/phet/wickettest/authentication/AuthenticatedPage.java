package edu.colorado.phet.wickettest.authentication;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;

import edu.colorado.phet.wickettest.data.PhetUser;
import edu.colorado.phet.wickettest.templates.PhetPage;
import edu.colorado.phet.wickettest.util.PhetSession;

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

}
