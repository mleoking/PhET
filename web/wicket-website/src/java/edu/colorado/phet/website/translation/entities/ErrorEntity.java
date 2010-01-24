package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.IndexPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class ErrorEntity extends TranslationEntity {
    public ErrorEntity() {
        addString( "error.internalError" );
    }

    public String getDisplayName() {
        return "Errors";
    }

}