package edu.colorado.phet.common.phetcommon.preferences;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

public class DefaultUpdatePreferences implements IPreferences {
    private PhetApplicationConfig phetApplicationConfig;

    public DefaultUpdatePreferences( PhetApplicationConfig phetApplicationConfig ) {
        this.phetApplicationConfig = phetApplicationConfig;
    }

    public boolean isApplyToAllSimulations() {
        return PhetPreferences.getInstance().isUpdatesApplyToAll();
    }

    public void setApplyToAllSimulations( boolean selected ) {
        boolean enabledForSelection = isEnabledForSelection();
        PhetPreferences.getInstance().setApplyUpdatesToAll( selected );
        setEnabledForSelection( enabledForSelection );
    }

    public void setEnabledForSelection( boolean selected ) {
        if ( isApplyToAllSimulations() ) {
            PhetPreferences.getInstance().setUpdatesEnabledForAll( selected );
        }
        else {
            PhetPreferences.getInstance().setUpdatesEnabled( phetApplicationConfig.getProjectName(), phetApplicationConfig.getFlavor(), selected );
        }
    }

    public boolean isEnabledForSelection() {
        if ( isApplyToAllSimulations() ) {
            return PhetPreferences.getInstance().isUpdatesEnabledForAll();
        }
        else {
            return PhetPreferences.getInstance().isUpdatesEnabled( phetApplicationConfig.getProjectName(), phetApplicationConfig.getFlavor() );
        }
    }
}
