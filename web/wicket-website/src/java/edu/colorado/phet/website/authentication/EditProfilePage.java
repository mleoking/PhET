package edu.colorado.phet.website.authentication;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.Linkable;

public class EditProfilePage extends PhetMenuPage {
    public EditProfilePage( PageParameters parameters ) {
        super( parameters );

        AuthenticatedPage.checkSignedIn();
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^edit-profile$", EditProfilePage.class );
    }

    public static Linkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "edit-profile";
            }
        };
    }
}
