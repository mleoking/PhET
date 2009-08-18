package edu.colorado.phet.wickettest.translation.entities;

import edu.colorado.phet.wickettest.content.IndexPanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class IndexEntity extends TranslationEntity {
    public IndexEntity() {
        addString( "home.header" );
        addString( "home.subheader" );
        addString( "home.playWithSims" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new IndexPanel( id, context );
            }
        }, "default" );
    }

    public String getDisplayName() {
        return "Home";
    }
}
