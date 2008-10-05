package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

/**
 * Adapter class to simplify uses of DefaultManualCheckForUpdates.
 */
public class ApplicationConfigManualCheckForUpdates extends DefaultManualCheckForUpdates {
    public ApplicationConfigManualCheckForUpdates( Window window, PhetApplicationConfig phetApplicationConfig ) {
        super( window, phetApplicationConfig.getProjectName(), phetApplicationConfig.getVersion(), phetApplicationConfig.getName() );
    }
}
