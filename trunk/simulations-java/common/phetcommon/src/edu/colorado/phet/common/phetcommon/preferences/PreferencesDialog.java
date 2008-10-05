package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.tracking.Tracker;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

public class PreferencesDialog extends JDialog {
    public PreferencesDialog( Frame owner, Tracker tracker, IManuallyCheckForUpdates iCheckForUpdates ) {
        super( owner, "Preferences", true );
        setContentPane( new PreferencesPanel( iCheckForUpdates, tracker ) );
        pack();
    }

    public static void main( String[] args ) {
//        PhetApplicationConfig config = new PhetApplicationConfig( args, new FrameSetup.CenteredWithSize( 1024, 768 ), new PhetResources( "nuclear-physics" ), "alpha-radiation" );
        PhetApplicationConfig config = new PhetApplicationConfig( args, new FrameSetup.CenteredWithSize( 1024, 768 ), new PhetResources( "balloons" ), "balloons" );
        Tracker tracker = config.getTracker();
        tracker.startTracking();
        PreferencesDialog preferencesDialog = new PreferencesDialog( null, tracker, new DefaultManualCheckForUpdates( config.getProjectName(), config.getVersion(), config.getName() ) );
        preferencesDialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        preferencesDialog.setVisible( true );
    }
}
