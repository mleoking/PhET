package edu.colorado.phet.common.phetcommon.updates;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

/**
 * Adapter class to simplify uses of DefaultManualCheckForUpdates.
 */
public class ApplicationConfigManualCheckForUpdates extends DefaultManualCheckForUpdates {
    public ApplicationConfigManualCheckForUpdates( Window window, PhetApplicationConfig phetApplicationConfig ) {
        super( window, phetApplicationConfig.getProjectName(), phetApplicationConfig.getFlavor(), phetApplicationConfig.getVersion(), phetApplicationConfig.getName() );
    }
}
