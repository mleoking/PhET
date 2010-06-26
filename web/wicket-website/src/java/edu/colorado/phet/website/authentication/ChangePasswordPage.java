package edu.colorado.phet.website.authentication;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.authentication.panels.ChangePasswordPanel;
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
        addTitle( new ResourceModel( "changePassword.title" ) );
        add( new ChangePasswordPanel( "set-password-panel", getPageContext() ) );
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^change-password$", ChangePasswordPage.class );
    }

    public static RawLinkable getLinker( ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "change-password";
            }
        };
    }
}
