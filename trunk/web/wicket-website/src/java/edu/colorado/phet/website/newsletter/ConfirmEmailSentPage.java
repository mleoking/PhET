package edu.colorado.phet.website.newsletter;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.templates.PhetMenuPage;

/**
 * Page after the initial subscribe, where a confirmation email is sent to the user.
 */
public class ConfirmEmailSentPage extends PhetMenuPage {

    private static Logger logger = Logger.getLogger( ConfirmEmailSentPage.class );

    public ConfirmEmailSentPage( PageParameters parameters ) {
        super( parameters );
        //setTitle( getLocalizer().getString( "resetPasswordCallback.title", this ) );
        setTitle( "Confirmation Email Sent" ); // TODO: i18nize

        add( new ConfirmEmailSentPanel( "main-panel", getPageContext() ) );

        hideSocialBookmarkButtons();
    }

}
