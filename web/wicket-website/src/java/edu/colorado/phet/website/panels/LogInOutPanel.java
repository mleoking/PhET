package edu.colorado.phet.website.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.admin.AdminMainPage;
import edu.colorado.phet.website.authentication.EditProfilePage;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.authentication.SignInPage;
import edu.colorado.phet.website.authentication.SignOutPage;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.util.PageContext;

/**
 * Panel that shows either the "Login / Register" link if not signed in, or the "Edit Profile | Sign out" links if
 * signed in.
 * <p/>
 * Additionally, if the user is a team member, an additional "administration" link is shown that will go to the
 * administration interface
 */
public class LogInOutPanel extends PhetPanel {

    public LogInOutPanel( String id, PageContext context ) {
        super( id, context );

        final PhetSession psession = PhetSession.get();

        if ( psession != null && psession.isSignedIn() ) {
            // user is signed in

            add( SignOutPage.getLinker().getLink( "sign-out", context, getPhetCycle() ) );
            add( EditProfilePage.getLinker().getLink( "edit-profile", context, getPhetCycle() ) );
            add( new InvisibleComponent( "sign-in" ) );
            add( new Label( "current-email", psession.getUser().getEmail() ) );
            if ( PhetSession.get().getUser().isTeamMember() ) {
                BookmarkablePageLink link = new BookmarkablePageLink<Void>( "admin-link", AdminMainPage.class );
                add( link );
            }
            else {
                add( new InvisibleComponent( "admin-link" ) );
            }
        }
        else {
            // user is not signed in

            add( new InvisibleComponent( "current-email" ) );
            add( new InvisibleComponent( "edit-profile" ) );
            add( new InvisibleComponent( "sign-out" ) );
            if ( DistributionHandler.displayLogin( getPhetCycle() ) ) {
                String path = getFullPath( context );
                add( SignInPage.getLinker( path ).getLink( "sign-in", context, getPhetCycle() ) );
            }
            else {
                add( new InvisibleComponent( "sign-in" ) );
            }
            add( new InvisibleComponent( "team-member" ) );
        }
    }

    private String getFullPath( PageContext context ) {
        String path = context.getPrefix() + context.getPath();
        String queryString = getPhetCycle().getQueryString();
        if ( queryString != null ) {
            path += "?" + queryString;
        }
        return path;
    }
}
