package edu.colorado.phet.wickettest.translation.entities;

import edu.colorado.phet.wickettest.content.ContributePanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

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