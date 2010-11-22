package edu.colorado.phet.website.newsletter;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * A page that allows a user to enter in their email address and press "subscribe".
 */
public class InitialSubscribePage extends PhetMenuPage {

    private static Logger logger = Logger.getLogger( InitialSubscribePage.class );

    public InitialSubscribePage( PageParameters parameters ) {
        super( parameters );
        setTitle( "Subscribe to PhET" ); // TODO: i18nize

        add( new InitialSubscribePanel( "main-panel", getPageContext() ) );

        hideSocialBookmarkButtons();
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^subscribe$", InitialSubscribePage.class );
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "subscribe";
            }
        };
    }
}
