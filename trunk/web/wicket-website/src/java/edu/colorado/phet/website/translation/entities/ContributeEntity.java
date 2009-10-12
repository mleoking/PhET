package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.ContributePanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class ContributeEntity extends TranslationEntity {
    public ContributeEntity() {
        addString( "contribute.financialContributions" );
        addString( "contribute.main" );
        addString( "contribute.thanks" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new ContributePanel( id, context );
            }
        }, "Contribute" );
    }

    public String getDisplayName() {
        return "Contribute";
    }
}