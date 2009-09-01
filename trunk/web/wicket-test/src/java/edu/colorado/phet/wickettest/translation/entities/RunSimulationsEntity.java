package edu.colorado.phet.wickettest.translation.entities;

import edu.colorado.phet.wickettest.content.OneAtATimePanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class RunSimulationsEntity extends TranslationEntity {

    public RunSimulationsEntity() {
        addString( "get-phet.one-at-a-time.runningSims" );
        addString( "get-phet.one-at-a-time.downloadingSims" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new OneAtATimePanel( id, context );
            }
        }, "Run Simulations - One at a Time" );
    }

    public String getDisplayName() {
        return "Run Simulations";
    }
}