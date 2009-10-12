package edu.colorado.phet.website.admin;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.authentication.AuthenticatedPage;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.data.PhetUser;

public class AdminPage extends AuthenticatedPage {
    public AdminPage( PageParameters parameters ) {
        super( parameters );

        PhetUser user = PhetSession.get().getUser();

        if ( !user.isTeamMember() ) {
            setResponsePage( getApplication().getApplicationSettings().getAccessDeniedPage() );
        }

        addTitle( "PhET Basic Administration" );
    }
}
