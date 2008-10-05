package edu.colorado.phet.common.phetcommon.preferences;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

public class DefaultUpdatePreferences implements IPreferences {
    private PhetApplicationConfig phetApplicationConfig;

    public DefaultUpdatePreferences( PhetApplicationConfig phetApplicationConfig ) {
        this.phetApplicationConfig = phetApplicationConfig;
    }

    public boolean isEnabledForSim() {
        return PhetPreferences.getInstance().isUpdatesEnabled( phetApplicationConfig.getProjectName(), phetApplicationConfig.getFlavor() );
    }

    public boolean isForAllSimulations() {
        return PhetPreferences.getInstance().isUpdatesEnabledForAll();
    }

    public void setForAllSimulations( boolean selected ) {
        PhetPreferences.getInstance().setUpdatesEnabledForAll( selected );
    }
}
