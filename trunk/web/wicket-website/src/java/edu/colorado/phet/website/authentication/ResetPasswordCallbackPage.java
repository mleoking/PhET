package edu.colorado.phet.website.authentication;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.authentication.panels.ResetPasswordCallbackPanel;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * @author Sam Reid
 */
public class ResetPasswordCallbackPage extends PhetMenuPage {
    public ResetPasswordCallbackPage( PageParameters parameters ) {
        super( parameters );
        addTitle( new ResourceModel( "resetPasswordCallback.title" ) );
        add( new ResetPasswordCallbackPanel( "reset-password-callback-panel", getPageContext(), parameters.getString( "key" ) ) );
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^reset-password-callback$", ResetPasswordCallbackPage.class );//Wicket automatically strips the "?key=..."
    }

    public static RawLinkable getLinker( final String key ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "reset-password-callback?key=" + key;
            }
        };
    }
}
