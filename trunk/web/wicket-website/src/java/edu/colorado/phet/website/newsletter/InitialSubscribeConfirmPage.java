package edu.colorado.phet.website.newsletter;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Page after the initial subscribe, where a confirmation email is sent to the user.
 */
public class InitialSubscribeConfirmPage extends PhetMenuPage {

    private static Logger logger = Logger.getLogger( InitialSubscribeConfirmPage.class );

    public InitialSubscribeConfirmPage( PageParameters parameters ) {
        super( parameters );
        //setTitle( getLocalizer().getString( "resetPasswordCallback.title", this ) );
        setTitle( "Confirmation Email Sent" ); // TODO: i18nize

        String param = parameters.getString( "key" );

        logger.warn( "param: " + param );

        add( new InitialSubscribeConfirmPanel( "main-panel", getPageContext() ) );

        hideSocialBookmarkButtons();
    }
}
