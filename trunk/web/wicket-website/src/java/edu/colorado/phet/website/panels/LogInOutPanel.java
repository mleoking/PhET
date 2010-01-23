package edu.colorado.phet.website.panels;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.authentication.EditProfilePage;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.authentication.SignInPage;
import edu.colorado.phet.website.authentication.SignOutPage;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.util.PageContext;

public class LogInOutPanel extends PhetPanel {

    // TODO: i18nize

    public LogInOutPanel( String id, PageContext context ) {
        super( id, context );

        final PhetSession psession = PhetSession.get();
        if ( psession != null && psession.isSignedIn() ) {
            add( SignOutPage.getLinker().getLink( "sign-out", context, getPhetCycle() ) );
            add( EditProfilePage.getLinker().getLink( "edit-profile", context, getPhetCycle() ) );
            add( new InvisibleComponent( "sign-in" ) );
        }
        else {
            add( new InvisibleComponent( "edit-profile" ) );
            add( new InvisibleComponent( "sign-out" ) );
            if ( DistributionHandler.displayLogin( getPhetCycle() ) ) {
                add( SignInPage.getLinker( context.getPrefix() + context.getPath() ).getLink( "sign-in", context, getPhetCycle() ) );
            }
            else {
                add( new InvisibleComponent( "sign-in" ) );
            }
        }
    }
}
