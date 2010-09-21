package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class TroubleshootingFlashEntity extends TranslationEntity {

    public TroubleshootingFlashEntity() {
        addString( "troubleshooting.flash.intro" );
        addString( "troubleshooting.flash.toRun" );
        addString( "troubleshooting.flash.blankWindow" );
        addString( "troubleshooting.flash.olderVersions" );
        addString( "troubleshooting.flash.q1.title" );
        addString( "troubleshooting.flash.q1.answer" );
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