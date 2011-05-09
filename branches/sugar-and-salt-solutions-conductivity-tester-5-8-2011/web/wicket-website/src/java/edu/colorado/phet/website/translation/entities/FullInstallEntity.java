package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.getphet.FullInstallPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class FullInstallEntity extends TranslationEntity {

    public FullInstallEntity() {
        addString( "get-phet.full-install.header" );
        addString( "get-phet.full-install.installerDescription" );
        addString( "get-phet.full-install.getJava" );
        addString( "get-phet.full-install.updatedFrequently" );
        addString( "get-phet.full-install.downloadWindows" );
        addString( "get-phet.full-install.downloadMac" );
        addString( "get-phet.full-install.downloadLinux" );
        addString( "get-phet.full-install.downloadInMB" );
        addString( "get-phet.full-install.contactForCD" );
        addString( "get-phet.full-install.helpAndTroubleshooting" );
        addString( "get-phet.full-install.troubleshootingInfo" );
        addString( "get-phet.full-install.supportSoftware" );
        addString( "get-phet.full-install.requirements" );
        addString( "get-phet.full-install.creatingInstallationCD" );
        addString( "get-phet.full-install.creatingInstallationCD.intro" );
        addString( "get-phet.full-install.creatingInstallationCD.instructions" );
        addString( "get-phet.full-install.creatingInstallationCD.step1" );
        addString( "get-phet.full-install.creatingInstallationCD.step2" );
        addString( "get-phet.full-install.creatingInstallationCD.step3" );
        addString( "get-phet.full-install.creatingInstallationCD.step4" );

        addString( "installer.mostUpToDate" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new FullInstallPanel( id, context );
            }
        }, "Full Installation Page" );
    }

    public String getDisplayName() {
        return "Full Installation";
    }
}