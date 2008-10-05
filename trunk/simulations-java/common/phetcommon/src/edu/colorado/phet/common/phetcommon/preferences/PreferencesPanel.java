package edu.colorado.phet.common.phetcommon.preferences;

import javax.swing.*;

public class PreferencesPanel extends JPanel {
    public PreferencesPanel( IManuallyCheckForUpdates iCheckForUpdates, ITrackingInfo tracker ) {
        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab( "Updates", new UpdatesPreferencesPanel( iCheckForUpdates ) );
        jTabbedPane.addTab( "Tracking", new TrackingPreferencesPanel( tracker ) );
        add( jTabbedPane );
    }
}
