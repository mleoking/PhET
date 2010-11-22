package edu.colorado.phet.website.newsletter;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.hibernate.Session;

import edu.colorado.phet.website.content.ErrorPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class SubscribeLandingPage extends PhetMenuPage {

    private static Logger logger = Logger.getLogger( SubscribeLandingPage.class );

    public SubscribeLandingPage( PageParameters parameters ) {
        super( parameters );
        //setTitle( getLocalizer().getString( "resetPasswordCallback.title", this ) );
        setTitle( "Subscribed to PhET Newsletter" ); // TODO: i18nize

        final String confirmationKey = parameters.getString( "key" );

        final PhetUser[] user = new PhetUser[1];

        boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                user[0] = NewsletterUtils.getUserFromConfirmationKey( getHibernateSession(), confirmationKey );
                if ( user[0] != null ) {
                    user[0].setReceiveEmail( true );
                    session.update( user[0] );
                    return true;
                }
                else {
                    logger.warn( "user not found for confirmationKey: " + confirmationKey );
                    return false;
                }
            }
        } );
        if ( !success ) {
            ErrorPage.redirectToErrorPage();
        }

        add( new SubscribeLandingPanel( "main-panel", getPageContext() ) );

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
