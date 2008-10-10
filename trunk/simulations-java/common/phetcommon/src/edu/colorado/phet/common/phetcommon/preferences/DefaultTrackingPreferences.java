package edu.colorado.phet.common.phetcommon.preferences;


public class DefaultTrackingPreferences implements ITrackingPreferences {

    public void setEnabled( boolean selected ) {
        PhetPreferences.getInstance().setTrackingEnabled( selected );
    }

    public boolean isEnabled() {
        return PhetPreferences.getInstance().isTrackingEnabled();
    }
}