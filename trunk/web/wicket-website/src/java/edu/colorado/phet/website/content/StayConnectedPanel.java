package edu.colorado.phet.website.content;

import edu.colorado.phet.website.authentication.EditProfilePage;
import edu.colorado.phet.website.authentication.RegisterPage;
import edu.colorado.phet.website.authentication.SignInPage;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class StayConnectedPanel extends PhetPanel {
    public StayConnectedPanel( String id, PageContext context ) {
        super( id, context );

        // TODO: try not to duplicate this
        add( new LocalizedText( "facebook-text", "home.facebookText", new Object[] {
                "<img class=\"index-social-image\" src=\"/images/icons/social/facebook.png\" alt=\"Facebook icon\"/>"
        } ) );
        add( new LocalizedText( "twitter-text", "home.twitterText", new Object[] {
                "<img class=\"index-social-image\" src=\"/images/icons/social/twitter.png\" alt=\"Twitter icon\"/>"
        } ) );
        add( new LocalizedText( "blog-text", "home.blogText" ) );

        add( new LocalizedText( "newsletter-instructions", "stayConnected.newsletterInstructions", new Object[]{
                SignInPage.getLinker( getFullPath( context ) ).getHref( context, getPhetCycle() ),
                RegisterPage.getLinker( getFullPath( context ) ).getHref( context, getPhetCycle() ),
                EditProfilePage.getLinker( getFullPath( context ) ).getHref( context, getPhetCycle() )
        }));
    }

    public static String getKey() {
        return "stayConnected";
    }

    public static String getUrl() {
        return "stay-connected";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}