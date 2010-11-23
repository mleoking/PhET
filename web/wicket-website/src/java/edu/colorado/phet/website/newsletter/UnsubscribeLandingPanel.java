package edu.colorado.phet.website.newsletter;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HtmlUtils;
import edu.colorado.phet.website.util.PageContext;

/**
 * The panel which represents the main content portion of the home (index) page
 */
public class UnsubscribeLandingPanel extends PhetPanel {
    public UnsubscribeLandingPanel( String id, PageContext context, PhetUser user ) {
        super( id, context );

        add( new LocalizedText( "now-unsubscribed", "newsletter.nowUnsubscribed", new Object[] {
                HtmlUtils.encode( user.getEmail() )
        } ) );
    }
}
