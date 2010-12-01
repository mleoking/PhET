package edu.colorado.phet.website.newsletter;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

public class ConfirmEmailLandingPanel extends PhetPanel {

    public static final int REDIRECTION_DELAY_SECONDS = 5;

    public ConfirmEmailLandingPanel( String id, PageContext context, PhetUser user, String destination ) {
        super( id, context );

        if ( user.isNewsletterOnlyAccount() ) {
            add( new LocalizedText( "now-subscribed", "newsletter.nowSubscribed" ) );
            add( new InvisibleComponent( "redirector" ) );
            add( new InvisibleComponent( "now-registered" ) );
        }
        else {
            add( new InvisibleComponent( "now-subscribed" ) );

            if ( destination != null ) {
                Label redirector = new Label( "redirector", "" );
                redirector.add( new AttributeModifier( "content", true, new Model<String>( REDIRECTION_DELAY_SECONDS + ";url=" + destination ) ) );
                add( redirector );
            }
            else {
                add( new InvisibleComponent( "redirector" ) );
            }

            add( new LocalizedText( "now-registered", "newsletter.nowRegistered" ) );
        }

        if ( destination != null ) {
            add( new LocalizedText( "redirection", "newsletter.redirection" ) );
        }
        else {
            add( new InvisibleComponent( "redirection" ) );
        }

    }
}
