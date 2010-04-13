package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.about.AboutSponsorsPanel;
import edu.colorado.phet.website.content.DonatePanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.panels.SponsorsPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class DonateEntity extends TranslationEntity {
    public DonateEntity() {
        addString( "donate.title" );
        addString( "donate.header" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new DonatePanel( id, context );
            }
        }, "Donate page" );
    }

    public String getDisplayName() {
        return "Donate";
    }
}