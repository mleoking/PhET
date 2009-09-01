package edu.colorado.phet.wickettest.translation.entities;

import edu.colorado.phet.wickettest.content.WorkshopsPanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class WorkshopsEntity extends TranslationEntity {
    public WorkshopsEntity() {
        addString( "workshops.intro" );
        addString( "workshops.upcomingWorkshops" );
        addString( "workshops.materials" );
        addString( "workshops.pastWorkshops" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new WorkshopsPanel( id, context );
            }
        }, "Workshops page" );
    }

    public String getDisplayName() {
        return "Workshops";
    }
}
