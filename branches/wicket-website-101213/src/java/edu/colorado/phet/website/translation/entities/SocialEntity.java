package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.StayConnectedPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class SocialEntity extends TranslationEntity {
    public SocialEntity() {
        addString( "stayConnected.title" );
        addString( "stayConnected.newsletterInstructions" );
        addString( "social.facebook.tooltip" );
        addString( "social.twitter.tooltip" );
        addString( "social.stumbleupon.tooltip" );
        addString( "social.digg.tooltip" );
        addString( "social.reddit.tooltip" );
        addString( "social.delicious.tooltip" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new StayConnectedPanel( id, context );
            }
        }, "Stay Connected" );
    }

    public String getDisplayName() {
        return "Social";
    }
}
