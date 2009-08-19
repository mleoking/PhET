package edu.colorado.phet.wickettest.translation.entities;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.translation.previews.TitlePreviewPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class TitlesEntity extends TranslationEntity {
    public TitlesEntity() {
        addString( "home.title", "Title of the home page." );
        addString( "simulationPage.title", "Title for a particular simulation. {0} will be replaced by the simulation title, and {1} {2} and {3} will be replaced by keywords." );
        addString( "simulationDisplay.title", "Title for a category of simulations. {0} will be replaced by the translated category name (like nav.motion)." );
        addString( "about.title", "Title of the About page" );
        addString( "workshops.title", "Title of the Workshops page" );
        addString( "contribute.title", "Title of the Contribute page" );
        addString( "get-phet.title", "Title of the 'Run our Simulations' page" );
        addString( "get-phet.one-at-a-time.title", "Title of the 'One at a Time' page" );
        addString( "get-phet.full-install.title", "Title of the 'Full Install' page" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new TitlePreviewPanel( id, context );
            }
        }, "default" );
    }

    public String getDisplayName() {
        return "Titles";
    }
}
