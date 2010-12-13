package edu.colorado.phet.website.newsletter;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.content.ErrorPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.hibernate.HibernateUtils;
import edu.colorado.phet.website.util.hibernate.Result;

/**
 * Page after the initial subscribe, where a confirmation email is sent to the user.
 */
public class ConfirmEmailSentPage extends PhetMenuPage {

    private static Logger logger = Logger.getLogger( ConfirmEmailSentPage.class );

    public ConfirmEmailSentPage( PageParameters parameters ) {
        super( parameters );

        final int userId = parameters.getInt( "userId" );
        Result<PhetUser> userResult = HibernateUtils.load( getHibernateSession(), PhetUser.class, userId );

        if ( !userResult.success ) {
            ErrorPage.redirectToErrorPage();
        }

        setTitle( getLocalizer().getString( "newsletter.confirmEmailSent.title", this ) );

        add( new ConfirmEmailSentPanel( "main-panel", getPageContext(), userResult.value ) );

        hideSocialBookmarkButtons();
    }

    public static PageParameters getParameters( PhetUser user ) {
        PageParameters params = new PageParameters();
        params.put( "userId", user.getId() );
        return params;
    }

}
