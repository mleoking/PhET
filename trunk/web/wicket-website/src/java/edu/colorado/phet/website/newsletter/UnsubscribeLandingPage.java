package edu.colorado.phet.website.newsletter;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.hibernate.Session;

import edu.colorado.phet.website.content.ErrorPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.hibernate.HibernateResult;
import edu.colorado.phet.website.util.hibernate.HibernateTask;
import edu.colorado.phet.website.util.hibernate.HibernateUtils;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Visiting unsubscribes the current user from the newsletter, and sends the "unsubscribed" email to the user.
 */
public class UnsubscribeLandingPage extends PhetMenuPage {

    private static Logger logger = Logger.getLogger( UnsubscribeLandingPage.class );

    public UnsubscribeLandingPage( PageParameters parameters ) {
        super( parameters );
        //setTitle( getLocalizer().getString( "resetPasswordCallback.title", this ) );
        setTitle( "Unsubscribed to PhET Newsletters" ); // TODO: i18nize

        final String confirmationKey = parameters.getString( "key" );

        final HibernateResult<PhetUser> userResult = new HibernateResult<PhetUser>();

        boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                PhetUser user = PhetUser.getUserFromConfirmationKey( getHibernateSession(), confirmationKey );
                userResult.setValue( user );
                if ( user != null ) {
                    user.setReceiveEmail( false );
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
            NewsletterUtils.sendUnsubscribedEmail( getPageContext(), userResult.getValue() );
        }
        else {
            ErrorPage.redirectToErrorPage();
        }

        logger.info( userResult.getValue().getEmail() + " unsubscribed" );

        add( new UnsubscribeLandingPanel( "main-panel", getPageContext(), userResult.getValue() ) );

        hideSocialBookmarkButtons();
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^unsubscribe$", UnsubscribeLandingPage.class );//Wicket automatically strips the "?key=..."
    }

    public static RawLinkable getLinker( final String key ) {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "unsubscribe?key=" + key;
            }
        };
    }
}
