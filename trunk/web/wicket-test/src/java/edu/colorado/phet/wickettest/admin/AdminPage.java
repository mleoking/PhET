package edu.colorado.phet.wickettest.admin;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.wickettest.authentication.AuthenticatedPage;
import edu.colorado.phet.wickettest.data.PhetUser;
import edu.colorado.phet.wickettest.util.PhetSession;

public class AdminPage extends AuthenticatedPage {
    public AdminPage( PageParameters parameters ) {
        super( parameters );

        PhetUser user = PhetSession.get().getUser();

        if ( !user.isTeamMember() ) {
            setResponsePage( getApplication().getApplicationSettings().getAccessDeniedPage() );
        }
    }
}
