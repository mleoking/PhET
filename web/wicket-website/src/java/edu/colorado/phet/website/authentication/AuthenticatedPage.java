package edu.colorado.phet.website.authentication;

import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.templates.PhetPage;

public class AuthenticatedPage extends PhetPage {

    private static final Logger logger = Logger.getLogger( AuthenticatedPage.class.getName() );

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

    public static void checkSignedIn( Set<NavLocation> navLocations ) {
        // TODO: change to collection?
        if ( !PhetSession.get().isSignedIn() ) {
            PageParameters params = new PageParameters();
            params.put( "navLocations", navLocations );

            throw new RestartResponseAtInterceptPageException( new SignInPage( params ) );
        }
    }

}
