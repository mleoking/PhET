package edu.colorado.phet.website.newsletter;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.hibernate.Session;

import edu.colorado.phet.website.content.ErrorPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.*;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Where the user lands when they click on the "confirm subscription" link from the confirmation email
 */
public class SubscribeLandingPage extends PhetMenuPage {

    private static Logger logger = Logger.getLogger( SubscribeLandingPage.class );

    public SubscribeLandingPage( PageParameters parameters ) {
        super( parameters );
        //setTitle( getLocalizer().getString( "resetPasswordCallback.title", this ) );
        setTitle( "Subscribed to PhET Newsletter" ); // TODO: i18nize

        final String confirmationKey = parameters.getString( "key" );

        final Result<PhetUser> userResult = new Result<PhetUser>();

        boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                PhetUser user = PhetUser.getUserFromConfirmationKey( getHibernateSession(), confirmationKey );
                userResult.setValue( user );
                if ( user != null ) {
                    user.setReceiveEmail( true );
                    user.setConfirmed( true );
                    session.update( user );
                    return true;
                }
                else {
                    logger.warn( "user not found for confirmationKey: " + confirmationKey );
                    return false;
                }
            }
        } );
        if ( success ) {
            boolean emailSuccess = NewsletterUtils.sendNewsletterWelcomeEmail( getPageContext(), userResult.getValue() );
            if ( !emailSuccess ) {
                // we are still OK if email fails, since this only lets them know about the success. Don't fail out.
                //ErrorPage.redirectToErrorPage();
            }
        }
        else {
            ErrorPage.redirectToErrorPage();
        }

        logger.info( userResult.getValue().getEmail() + " subscribed" );

        add( new SubscribeLandingPanel( "main-panel", getPageContext(), userResult.getValue() ) );

        hideSocialBookmarkButtons();
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^confirm-subscription$", SubscribeLandingPage.class );//Wicket automatically strips the "?key=..."
    }

    public static RawLinkable getLinker( final String key ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "confirm-subscription?key=" + key;
            }
        };
    }
}
