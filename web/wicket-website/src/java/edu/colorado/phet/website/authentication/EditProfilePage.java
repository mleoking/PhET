package edu.colorado.phet.website.authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.authentication.panels.EditProfilePanel;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.Linkable;
import edu.colorado.phet.website.util.links.RawLinkable;

public class EditProfilePage extends PhetMenuPage {

    private static final Logger logger = Logger.getLogger( EditProfilePage.class.getName() );

    public EditProfilePage( PageParameters parameters ) {
        super( parameters );

        verifySignedIn();

        String destination = null;

        if ( parameters != null && parameters.containsKey( "dest" ) ) {
            destination = parameters.getString( "dest" );
        }

        addTitle( new ResourceModel( "editProfile.title" ) );

        add( new EditProfilePanel( "edit-profile-panel", getPageContext(), destination ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^edit-profile$", EditProfilePage.class );
    }

//    public static Linkable getLinker() {
//        return new AbstractLinker() {
//            @Override
//            public String getSubUrl( PageContext context ) {
//                return "edit-profile";
//            }
//        };
//    }

    public static RawLinkable getLinker( final String destination ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                try {
                    // always encode the destination because special characters might be hidden
                    String finalDestination = destination;
                    if ( finalDestination.equals( "/en/" ) ) {
                        finalDestination = "/";
                    }
                    return "edit-profile?dest=" + URLEncoder.encode( finalDestination, "UTF-8" );
                }
                catch( UnsupportedEncodingException e ) {
                    e.printStackTrace();
                    throw new RuntimeException( e );
                }
            }
        };
    }

}
