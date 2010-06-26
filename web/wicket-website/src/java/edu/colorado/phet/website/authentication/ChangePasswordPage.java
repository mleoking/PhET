package edu.colorado.phet.website.authentication;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.authentication.panels.SetPasswordPanel;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * @author Sam Reid
 */
public class ChangePasswordPage extends PhetMenuPage {
    public ChangePasswordPage( PageParameters parameters ) {
        super( parameters );
        addTitle( "Change Password" );
        add( new SetPasswordPanel( "set-password-panel", getPageContext() ) );
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^change-password$", ChangePasswordPage.class );
    }

    public static RawLinkable getLinker( final String destination ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "change-password";
            }
        };
    }
}
