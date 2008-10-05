package edu.colorado.phet.common.phetcommon.preferences;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

/**
 * Adapter class to simplify uses of DefaultManualCheckForUpdates.
 */
public class ApplicationConfigManualCheckForUpdates extends DefaultManualCheckForUpdates {
    public ApplicationConfigManualCheckForUpdates( PhetApplicationConfig phetApplicationConfig ) {
        super( phetApplicationConfig.getProjectName(), phetApplicationConfig.getVersion(), phetApplicationConfig.getName() );
    }
}
