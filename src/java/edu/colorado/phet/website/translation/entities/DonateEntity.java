package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.DonatePanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class DonateEntity extends TranslationEntity {
    public DonateEntity() {
        addString( "donate.title" );
        addString( "donate.header" );
        addString( "donate.ifUseSimulations" );
        addString( "donate.anySize" );
        addString( "donate.donateNow" );
        addString( "donate.questions" );
        addString( "donate.note" );

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