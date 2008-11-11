package edu.colorado.phet.common.phetcommon.updates;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;

/**
 * Adapter class to simplify uses of DefaultManualCheckForUpdates.
 */
public class ApplicationConfigManualCheckForUpdates extends DefaultManualUpdateChecker {
    public ApplicationConfigManualCheckForUpdates( Frame frame, ISimInfo phetApplicationConfig ) {
        super( frame, phetApplicationConfig.getProjectName(), phetApplicationConfig.getFlavor(), phetApplicationConfig.getVersion(), phetApplicationConfig.getName(),phetApplicationConfig.getLocaleString() );
    }
}
