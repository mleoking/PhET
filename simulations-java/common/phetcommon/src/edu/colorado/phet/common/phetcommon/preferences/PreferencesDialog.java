package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.tracking.Tracker;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

public class PreferencesDialog extends JDialog {
    public PreferencesDialog( Frame owner, Tracker tracker ) {
        super( owner, "Preferences", true );
        setContentPane( new PreferencesPanel( tracker ) );
        pack();
    }

    public static void main( String[] args ) {
        PhetApplicationConfig config = new PhetApplicationConfig( args, new FrameSetup.CenteredWithSize( 1024, 768 ), new PhetResources( "nuclear-physics" ), "alpha-radiation" );
        Tracker tracker = config.getTracker();
        tracker.startTracking();
        new PreferencesDialog( null, tracker ).setVisible( true );
    }
}
