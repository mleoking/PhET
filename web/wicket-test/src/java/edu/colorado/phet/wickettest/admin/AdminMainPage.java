package edu.colorado.phet.wickettest.admin;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.wickettest.authentication.AuthenticatedPage;

public class AdminMainPage extends AuthenticatedPage {
    public AdminMainPage( PageParameters parameters ) {
        super( parameters );

        addTitle( "Administration section" );
    }
}
