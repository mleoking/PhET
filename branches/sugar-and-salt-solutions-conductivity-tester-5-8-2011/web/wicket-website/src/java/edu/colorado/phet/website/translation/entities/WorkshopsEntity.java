package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.workshops.WorkshopsPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class WorkshopsEntity extends TranslationEntity {
    public WorkshopsEntity() {
        addString( "workshops.intro" );
        addString( "workshops.upcomingWorkshops" );
        addString( "workshops.materials" );
        addString( "workshops.pastWorkshops" );
        addString( "nav.workshops.uganda" );
        addString( "workshops.uganda.title" );
        addString( "nav.workshops.uganda-photos" );
        addString( "workshops.uganda-photos.title" );

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
