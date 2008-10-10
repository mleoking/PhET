package edu.colorado.phet.common.phetcommon.preferences;


public class DefaultUpdatePreferences implements IUpdatesPreferences {

    public void setEnabled( boolean selected ) {
        PhetPreferences.getInstance().setUpdatesEnabled( selected );
    }

    public boolean isEnabled() {
        return PhetPreferences.getInstance().isUpdatesEnabled();
    }
}
