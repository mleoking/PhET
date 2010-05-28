package edu.colorado.phet.website.authentication;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.templates.PhetPage;

/**
 * Class and methods for requiring authentication (user log in) before viewing a page
 */
public class AuthenticatedPage extends PhetPage {

    private static final Logger logger = Logger.getLogger( AuthenticatedPage.class.getName() );

    public AuthenticatedPage( PageParameters parameters ) {
        super( parameters, true );

        if ( !PhetSession.get().isSignedIn() ) {
            throw new RestartResponseAtInterceptPageException( SignInPage.class );
        }

    }

    /**
     * Shortcut for getting the user
     *
     * @return The logged-in phet user
     */
    public PhetUser getUser() {
        return PhetSession.get().getUser();
    }

    /**
     * Check whether the user is signed in. If not, redirect to the sign-in page.
     */
    public static void checkSignedIn() {
        if ( !PhetSession.get().isSignedIn() ) {
            throw new RestartResponseAtInterceptPageException( SignInPage.class );
        }
    }

    /**
     * Check whether the user is signed in. If not, redirect to the sign-in page with the following nav locations for
     * the navigation menu
     *
     * @param navLocations Navigation locations for the navigation menu
     */
    public static void checkSignedIn( Collection<NavLocation> navLocations ) {
        // TODO: change to collection?
        if ( !PhetSession.get().isSignedIn() ) {
            PageParameters params = new PageParameters();
            params.put( "navLocations", navLocations );

            throw new RestartResponseAtInterceptPageException( new SignInPage( params ) );
        }
    }

}
