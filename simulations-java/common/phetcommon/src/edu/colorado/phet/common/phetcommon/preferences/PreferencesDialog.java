package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.updates.ApplicationConfigManualCheckForUpdates;

public class PreferencesDialog extends JDialog {
    public PreferencesDialog( Frame owner, ITrackingInfo tracker, IManualUpdateChecker iCheckForUpdates, IPreferences updatePreferences, IPreferences trackingPreferences ) {
        super( owner, "Preferences", true );
        setContentPane( new PreferencesPanel( iCheckForUpdates, tracker, updatePreferences, trackingPreferences ) );
        pack();
        if ( owner != null ) {
            SwingUtils.centerDialogInParent( this );
        }
        else {
            SwingUtils.centerWindowOnScreen( this );
        }
    }

    public static void main( String[] args ) {
        final PhetApplicationConfig config = new PhetApplicationConfig( args, new FrameSetup.CenteredWithSize( 1024, 768 ), new PhetResources( "balloons" ), "balloons" );
        PreferencesDialog preferencesDialog = new PreferencesDialog( null, config, new ApplicationConfigManualCheckForUpdates( null, config ), new DummyPreferences(), new DummyPreferences() );
        preferencesDialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        preferencesDialog.setVisible( true );
    }
}
