package edu.colorado.phet.website.newsletter;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.templates.PhetMenuPage;

/**
 * Page after the initial subscribe, where a confirmation email is sent to the user.
 */
public class InitialSubscribeConfirmPage extends PhetMenuPage {

    private static Logger logger = Logger.getLogger( InitialSubscribeConfirmPage.class );

    public static final String KEY = "key";

    public InitialSubscribeConfirmPage( PageParameters parameters ) {
        super( parameters );
        //setTitle( getLocalizer().getString( "resetPasswordCallback.title", this ) );
        setTitle( "Confirmation Email Sent" ); // TODO: i18nize

        String param = parameters.getString( KEY );

        logger.warn( "param: " + param );

        add( new InitialSubscribeConfirmPanel( "main-panel", getPageContext() ) );

        hideSocialBookmarkButtons();
    }

}
