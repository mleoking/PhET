package edu.colorado.phet.common.phetcommon.preferences;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

public class DefaultTrackingPreferences implements IPreferences {
    private PhetApplicationConfig phetApplicationConfig;

    public DefaultTrackingPreferences( PhetApplicationConfig phetApplicationConfig ) {
        this.phetApplicationConfig = phetApplicationConfig;
    }

    public boolean isEnabledForSim() {
        return PhetPreferences.getInstance().isTrackingEnabled( phetApplicationConfig.getProjectName(), phetApplicationConfig.getFlavor() );
    }

    public boolean isForAllSimulations() {
        return PhetPreferences.getInstance().isTrackingEnabledForAll();
    }

    public void setForAllSimulations( boolean selected ) {
        PhetPreferences.getInstance().setApplyTrackingToAll( selected );
    }
}