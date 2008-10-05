package edu.colorado.phet.common.phetcommon.preferences;

import javax.swing.*;

public class PreferencesPanel extends JPanel {
    public PreferencesPanel( IManuallyCheckForUpdates iCheckForUpdates, ITrackingInfo tracker, IPreferences updatePreferences, IPreferences trackingPreferences ) {
        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab( "Updates", new UpdatesPreferencesPanel( iCheckForUpdates, updatePreferences ) );
        jTabbedPane.addTab( "Tracking", new TrackingPreferencesPanel( tracker, trackingPreferences ) );
        add( jTabbedPane );
    }
}
