package edu.colorado.phet.website.newsletter;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
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

        add( new UnsubscribeLandingPanel( "main-panel", getPageContext() ) );

        String param = parameters.getString( "key" );
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
