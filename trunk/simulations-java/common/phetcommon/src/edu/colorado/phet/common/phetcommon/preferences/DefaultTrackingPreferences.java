package edu.colorado.phet.common.phetcommon.preferences;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

public class DefaultTrackingPreferences implements IPreferences {
    private PhetApplicationConfig phetApplicationConfig;

    public DefaultTrackingPreferences( PhetApplicationConfig phetApplicationConfig ) {
        this.phetApplicationConfig = phetApplicationConfig;
    }

    public boolean isApplyToAllSimulations() {
        return PhetPreferences.getInstance().isTrackingApplyToAll();
    }

    public void setApplyToAllSimulations( boolean selected ) {
        //store value and apply to new selection
        boolean enabledForSelection = isEnabledForSelection();
        PhetPreferences.getInstance().setApplyTrackingToAll( selected );
        setEnabledForSelection( enabledForSelection );
    }

    public void setEnabledForSelection( boolean selected ) {
        if ( isApplyToAllSimulations() ) {
            PhetPreferences.getInstance().setTrackingEnabledForAll( selected );
        }
        else {
            PhetPreferences.getInstance().setTrackingEnabled( phetApplicationConfig.getProjectName(), phetApplicationConfig.getFlavor(), selected );
        }
    }

    public boolean isEnabledForSelection() {
        if ( isApplyToAllSimulations() ) {
            return PhetPreferences.getInstance().isTrackingEnabledForAll();
        }
        else {
            return PhetPreferences.getInstance().isTrackingEnabled( phetApplicationConfig.getProjectName(), phetApplicationConfig.getFlavor() );
        }
    }
}