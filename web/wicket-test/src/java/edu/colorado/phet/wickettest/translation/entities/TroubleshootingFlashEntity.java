package edu.colorado.phet.wickettest.translation.entities;

import edu.colorado.phet.wickettest.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class TroubleshootingFlashEntity extends TranslationEntity {

    public TroubleshootingFlashEntity() {
        addString( "troubleshooting.flash.intro" );
        addString( "troubleshooting.flash.toRun" );
        addString( "troubleshooting.flash.blankWindow" );
        addString( "troubleshooting.flash.olderVersions" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new TroubleshootingFlashPanel( id, context );
            }
        }, "Troubleshooting (flash)" );
    }

    public String getDisplayName() {
        return "Troubleshooting (flash)";
    }
}