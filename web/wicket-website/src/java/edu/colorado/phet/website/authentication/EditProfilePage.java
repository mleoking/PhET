package edu.colorado.phet.website.authentication;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.authentication.panels.EditProfilePanel;
import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.Linkable;

public class EditProfilePage extends PhetPage {

    private static final Logger logger = Logger.getLogger( EditProfilePage.class.getName() );

    public EditProfilePage( PageParameters parameters ) {
        super( parameters );

        verifySignedIn();

        addTitle( new ResourceModel( "editProfile.title" ) );

        add( new EditProfilePanel( "edit-profile-panel", getPageContext() ) );

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
