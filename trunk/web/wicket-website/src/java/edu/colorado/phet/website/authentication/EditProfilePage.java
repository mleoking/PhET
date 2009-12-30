package edu.colorado.phet.website.authentication;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.templates.PhetMenuPage;

public class EditProfilePage extends PhetMenuPage {
    public EditProfilePage( PageParameters parameters ) {
        super( parameters );

        AuthenticatedPage.checkSignedIn();
    }
}
