package edu.colorado.phet.website.authentication;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.authentication.panels.ResetPasswordRequestSuccessPanel;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * @author Sam Reid
 */
public class ResetPasswordRequestSuccessPage extends PhetMenuPage {
    private static final Logger logger = Logger.getLogger( SignInPage.class.getName() );

    public ResetPasswordRequestSuccessPage( PageParameters parameters ) {
        super( parameters );

        setTitle( getLocalizer().getString( "resetPasswordRequestSuccess.title", this ) );
        add( new ResetPasswordRequestSuccessPanel( "reset-password-request-success", getPageContext() ) );

        hideSocialBookmarkButtons();
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "reset-password-request-success";
            }
        };
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^reset-password-request-success$", ResetPasswordRequestSuccessPage.class );
    }
}